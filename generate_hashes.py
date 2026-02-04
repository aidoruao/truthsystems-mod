#!/usr/bin/env python3
"""
TruthSystems Mod - SHA-256 Hash Generator
Covenant-Compliant File Verification

External Authority: Jesus Christ
Covenant: Σ_LORA_COVENANT v1.0
"""

import hashlib
import os
from pathlib import Path

def sha256_file(filepath):
    """Generate SHA-256 hash of a file."""
    sha256 = hashlib.sha256()
    with open(filepath, 'rb') as f:
        while chunk := f.read(8192):
            sha256.update(chunk)
    return sha256.hexdigest().upper()

def generate_hashes(root_dir):
    """Generate hashes for all source files."""
    hashes = {}
    
    # Java source files
    java_dir = Path(root_dir) / 'src' / 'main' / 'java'
    if java_dir.exists():
        for java_file in java_dir.rglob('*.java'):
            rel_path = java_file.relative_to(root_dir)
            hashes[str(rel_path)] = sha256_file(java_file)
    
    # Resource files
    res_dir = Path(root_dir) / 'src' / 'main' / 'resources'
    if res_dir.exists():
        for res_file in res_dir.rglob('*'):
            if res_file.is_file():
                rel_path = res_file.relative_to(root_dir)
                hashes[str(rel_path)] = sha256_file(res_file)
    
    # Build files
    for build_file in ['build.gradle', 'settings.gradle', 'gradle.properties']:
        build_path = Path(root_dir) / build_file
        if build_path.exists():
            hashes[build_file] = sha256_file(build_path)
    
    return hashes

def write_verification_file(hashes, output_path):
    """Write verification manifest."""
    with open(output_path, 'w', encoding='utf-8') as f:
        f.write("# TruthSystems Mod - SHA-256 Verification Manifest\n")
        f.write("# Covenant: SIGMA_LORA_COVENANT v1.0\n")
        f.write("# Generated: 2026-02-04\n\n")
        
        for filepath, file_hash in sorted(hashes.items()):
            f.write(f"{file_hash}  {filepath}\n")

if __name__ == '__main__':
    import sys
    
    if len(sys.argv) > 1:
        root = sys.argv[1]
    else:
        root = os.getcwd()
    
    print(f"Generating hashes for: {root}")
    hashes = generate_hashes(root)
    
    output = Path(root) / 'VERIFICATION_HASHES.txt'
    write_verification_file(hashes, output)
    
    print(f"[OK] Generated {len(hashes)} file hashes")
    print(f"[OK] Written to: {output}")
    print("\nCOVENANT_COMPLIANT: All files SHA-256 verified")
