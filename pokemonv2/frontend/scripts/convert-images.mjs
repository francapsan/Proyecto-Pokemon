/**
 * Convierte todas las imágenes PNG y JPG de public/media/ a WebP.
 * Uso:
 *   node scripts/convert-images.mjs               (calidad 82, mantiene los originales)
 *   node scripts/convert-images.mjs --q 90        (calidad personalizada)
 *   node scripts/convert-images.mjs --replace     (borra los originales tras convertir)
 *
 * El WebP se guarda junto al original con la misma base y extensión .webp.
 */
import { readdir, stat, unlink } from 'node:fs/promises';
import { extname, join, basename } from 'node:path';
import { fileURLToPath } from 'node:url';
import sharp from 'sharp';

const __dirname = fileURLToPath(new URL('.', import.meta.url));
const MEDIA_DIR = join(__dirname, '..', 'public', 'media');
const EXTS = new Set(['.png', '.jpg', '.jpeg']);

const qArg = process.argv.find((a, i) => process.argv[i - 1] === '--q');
const QUALITY = qArg ? Number(qArg) : 82;
const REPLACE = process.argv.includes('--replace');

const formatBytes = (bytes) => {
    if (bytes < 1024) return `${bytes} B`;
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
    return `${(bytes / (1024 * 1024)).toFixed(2)} MB`;
};

const convert = async () => {
    const entries = await readdir(MEDIA_DIR);
    const targets = entries.filter((name) => EXTS.has(extname(name).toLowerCase()));

    if (targets.length === 0) {
        console.log('No hay imágenes PNG/JPG que convertir en', MEDIA_DIR);
        return;
    }

    console.log(
        `Convirtiendo ${targets.length} archivo(s) a WebP (calidad ${QUALITY}${REPLACE ? ', borrando originales' : ''})...`
    );
    let totalIn = 0;
    let totalOut = 0;

    for (const name of targets) {
        const src = join(MEDIA_DIR, name);
        const dst = join(MEDIA_DIR, `${basename(name, extname(name))}.webp`);

        const { size: sizeIn } = await stat(src);
        await sharp(src).webp({ quality: QUALITY }).toFile(dst);
        const { size: sizeOut } = await stat(dst);

        totalIn += sizeIn;
        totalOut += sizeOut;

        const savings = (100 - (sizeOut / sizeIn) * 100).toFixed(1);
        console.log(
            `  ${name.padEnd(24)} ${formatBytes(sizeIn).padStart(10)} → ${formatBytes(sizeOut).padStart(10)}  (-${savings}%)`
        );

        if (REPLACE) {
            await unlink(src);
        }
    }

    const totalSavings = (100 - (totalOut / totalIn) * 100).toFixed(1);
    console.log(
        `\nTotal: ${formatBytes(totalIn)} → ${formatBytes(totalOut)}  (-${totalSavings}%)`
    );
};

convert().catch((err) => {
    console.error('Error convirtiendo imágenes:', err);
    process.exit(1);
});
