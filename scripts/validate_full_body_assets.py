"""Validate archived source identity and complete PNG streams without dependencies."""
import hashlib
import json
from pathlib import Path
import struct
import zlib

record = json.loads(Path('art/production/full-body/extraction.json').read_text())
checked = set()
for pose, info in record['poses'].items():
    source = Path('art/source/full-body') / info['source']
    assert hashlib.sha256(source.read_bytes()).hexdigest() == info['sha256'], source
    for path in (source, Path('app/src/main/res/drawable-nodpi') / f'grump_full_{pose}.png'):
        if path in checked:
            continue
        data = path.read_bytes()
        assert data[:8] == b'\x89PNG\r\n\x1a\n', path
        offset = 8
        ended = False
        while offset < len(data):
            length, = struct.unpack('>I', data[offset:offset+4])
            kind = data[offset+4:offset+8]
            chunk = data[offset+8:offset+8+length]
            crc, = struct.unpack('>I', data[offset+8+length:offset+12+length])
            assert zlib.crc32(kind + chunk) & 0xffffffff == crc, path
            if kind == b'IHDR':
                width, height, depth, color = struct.unpack('>IIBB', chunk[:10])
                assert depth == 8 and color == 6, (path, 'RGBA required')
                if path != source:
                    assert (width, height) == tuple(record['canvas']), path
            offset += 12 + length
            if kind == b'IEND':
                ended = True
                break
        assert ended and offset == len(data), (path, 'Incomplete PNG')
        checked.add(path)
print(f'Validated {len(checked)} complete RGBA PNGs and all source SHA-256 hashes.')
