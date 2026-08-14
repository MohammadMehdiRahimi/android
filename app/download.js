const https = require('https');
const fs = require('fs');

function download(url, dest) {
  return new Promise((resolve, reject) => {
    https.get(url, (res) => {
      if (res.statusCode === 302 || res.statusCode === 301) {
        return resolve(download(res.headers.location, dest));
      }
      if (res.statusCode !== 200) {
        return reject(new Error('Failed ' + res.statusCode));
      }
      const file = fs.createWriteStream(dest);
      res.pipe(file);
      file.on('finish', () => {
        file.close(resolve);
      });
    }).on('error', reject);
  });
}

async function main() {
  try {
    await download('https://raw.githubusercontent.com/google/fonts/main/ofl/vazirmatn/Vazirmatn-Regular.ttf', '/app/applet/app/src/main/res/font/vazirmatn_regular.ttf');
    await download('https://raw.githubusercontent.com/google/fonts/main/ofl/vazirmatn/Vazirmatn-Bold.ttf', '/app/applet/app/src/main/res/font/vazirmatn_bold.ttf');
    console.log('done');
  } catch (err) {
    console.error(err);
    process.exit(1);
  }
}

main();
