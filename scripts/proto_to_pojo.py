#!/usr/bin/env python3
"""
Simple generator: parse generated proto Java files under
`market-common/src/main/java/com/ganten/market/common/proto` and generate
plain POJO classes (package: com.ganten.market.common.pojo) with public
fields and simple fromProto/toProto methods for selected message names.

Usage:
  python3 scripts/proto_to_pojo.py CandleData,Tick,Trade

This is a best-effort tool intended to help extract data structures for
manual review. It doesn't fully parse Java; it's tuned for protoc-generated
Java sources layout used in this repo.
"""
import sys
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
PROTO_GEN_DIR = ROOT / 'market-common' / 'src' / 'main' / 'java' / 'com' / 'ganten' / 'market' / 'common' / 'proto'
OUT_PKG_DIR = ROOT / 'market-common' / 'src' / 'main' / 'java' / 'com' / 'ganten' / 'market' / 'common' / 'pojo'
OUT_PKG = 'com.ganten.market.common.pojo'

MSG_NAMES = []
if len(sys.argv) > 1:
    MSG_NAMES = sys.argv[1].split(',')
else:
    # default example set
    MSG_NAMES = ['CandleData', 'Last24HData', 'Tick', 'Trade', 'OrderBook', 'OrderBookResponse']

if not PROTO_GEN_DIR.exists():
    print('Proto generated directory not found:', PROTO_GEN_DIR)
    sys.exit(1)

OUT_PKG_DIR.mkdir(parents=True, exist_ok=True)

# load all generated proto java sources into memory
java_files = list(PROTO_GEN_DIR.glob('*.java'))
source_map = {}
for f in java_files:
    source_map[f.name] = f.read_text()

# helper to find message class source by name

def find_message_source(name):
    for fname, text in source_map.items():
        # search for "public static final class <name>"
        m = re.search(r'public\s+static\s+final\s+class\s+' + re.escape(name) + r'\b', text)
        if m:
            # extract the class block by finding opening brace and matching to its closing
            start = m.start()
            brace = text.find('{', start)
            if brace == -1:
                return None
            # crude brace matching
            i = brace + 1
            depth = 1
            while i < len(text) and depth > 0:
                if text[i] == '{':
                    depth += 1
                elif text[i] == '}':
                    depth -= 1
                i += 1
            return text[brace+1:i-1]
    return None

# extract getters and return types from class body
getter_re = re.compile(r'public\s+([\w\.<>, ?]+)\s+get([A-Z][A-Za-z0-9_]*)\s*\(')
list_getter_re = re.compile(r'public\s+java.util.List<([^>]+)>\s+get([A-Z][A-Za-z0-9_]*)List\s*\(')

for name in MSG_NAMES:
    body = find_message_source(name)
    if not body:
        print(f'WARNING: message class {name} not found in generated sources')
        continue
    props = []
    # list getters
    for m in list_getter_re.finditer(body):
        typ = m.group(1).strip()
        prop = m.group(2)
        props.append((prop, f'java.util.List<{typ}>'))
    for m in getter_re.finditer(body):
        typ = m.group(1).strip()
        prop = m.group(2)
        # skip List getters matched above
        if any(prop == p[0] for p in props):
            continue
        props.append((prop, typ))

    # build POJO source
    fields = []
    assigns_from = []
    assigns_to = []
    imports = set()
    for prop, typ in props:
        # prop name lowerCamel
        field_name = prop[0].lower() + prop[1:]
        # simplify type
        simple_typ = typ.replace('java.lang.', '')
        if simple_typ.startswith('com.google.protobuf'):
            imports.add(simple_typ)
            simple_typ = simple_typ.split('.')[-1]
        if simple_typ.startswith('java.util.List') or simple_typ.startswith('List<'):
            imports.add('java.util.List')
            simple_typ = simple_typ.replace('java.util.', '')
        fields.append((simple_typ, field_name))
        # fromProto assignment (best effort)
        getter = 'get' + prop + '()'
        if 'List<' in typ:
            assigns_from.append(f'        pojo.{field_name} = new java.util.ArrayList<>(proto.{getter});')
            assigns_to.append(f'        outBuilder.addAll{prop}(this.{field_name});')
            imports.add('java.util.ArrayList')
        else:
            assigns_from.append(f'        pojo.{field_name} = proto.{getter};')
            assigns_to.append(f'        outBuilder.set{prop}(this.{field_name} == null ? "" : this.{field_name});')

    class_name = name
    out_lines = []
    out_lines.append(f'package {OUT_PKG};')
    out_lines.append('')
    if imports:
        for im in sorted(imports):
            out_lines.append(f'import {im};')
        out_lines.append('')
    out_lines.append('/**')
    out_lines.append(f' * POJO generated from proto message {name} (best-effort).')
    out_lines.append(' * Only contains simple public fields and conversion helpers.')
    out_lines.append(' */')
    out_lines.append(f'public class {class_name} ' + '{')
    out_lines.append('')
    for typ, fname in fields:
        out_lines.append(f'    public {typ} {fname};')
    out_lines.append('')
    # fromProto
    out_lines.append(f'    public static {class_name} fromProto(com.ganten.market.common.proto.{name} proto) ' + '{')
    out_lines.append(f'        if (proto == null) return null;')
    out_lines.append(f'        {class_name} pojo = new {class_name}();')
    if assigns_from:
        out_lines.extend(assigns_from)
    out_lines.append('        return pojo;')
    out_lines.append('    }')
    out_lines.append('')
    # toProto
    out_lines.append(f'    public com.ganten.market.common.proto.{name} toProto() ' + '{')
    out_lines.append(f'        com.ganten.market.common.proto.{name}.Builder outBuilder = com.ganten.market.common.proto.{name}.newBuilder();')
    if assigns_to:
        out_lines.extend(assigns_to)
    out_lines.append('        return outBuilder.build();')
    out_lines.append('    }')

    out_lines.append('}')

    out_src = '\n'.join(out_lines)
    out_path = OUT_PKG_DIR / f'{class_name}.java'
    out_path.write_text(out_src)
    print('WROTE', out_path)

print('Done.')
