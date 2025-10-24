#!/usr/bin/env python3
"""
For every .java under src/main/java, replace the very first non-empty line with the package declaration
derived from the path. Backup original first line to <file>.firstline.bak
"""
import os
import re

root = os.path.dirname(os.path.dirname(__file__))
count = 0
changed_files = []
for dirpath, dirnames, filenames in os.walk(root):
    if 'src' + os.sep + 'main' + os.sep + 'java' not in dirpath.replace('\\','/'):
        continue
    for fn in filenames:
        if not fn.endswith('.java'):
            continue
        path = os.path.join(dirpath, fn)
        rel = path.split(os.path.join('src','main','java') + os.sep,1)[1]
        pkg = rel.rsplit(os.sep,1)[0].replace(os.sep, '.')
        # Build package declaration
        package_decl = f'package {pkg};\n'
        # Read file lines
        with open(path, 'r', encoding='utf-8') as f:
            lines = f.readlines()
        if not lines:
            continue
        # find index of first non-empty line (striped)
        idx = 0
        while idx < len(lines) and lines[idx].strip() == '':
            idx += 1
        if idx >= len(lines):
            continue
        original_first_line = lines[idx]
        # If the first non-empty line is already the desired package declaration, skip
        if original_first_line.strip() == package_decl.strip():
            continue
        # backup original first line
        bak_path = path + '.firstline.bak'
        with open(bak_path, 'a', encoding='utf-8') as bf:
            bf.write(original_first_line)
        # replace that first non-empty line
        lines[idx] = package_decl
        with open(path, 'w', encoding='utf-8') as f:
            f.writelines(lines)
        count += 1
        changed_files.append(path)

print(f"Replaced first non-empty line in {count} files")
for p in changed_files[:200]:
    print(p)
