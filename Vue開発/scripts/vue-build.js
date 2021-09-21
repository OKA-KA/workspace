const childProcess = require('child_process');

// コマンドの設定
const cmd = `vue-cli-service build --target lib src/kintone-vue.js`;

// コマンドの実行
const spawn = childProcess.spawn(cmd, { shell: true });

spawn.stdout.on('data', (data) => {
  console.log(data.toString());
});
spawn.stderr.on('data', (data) => {
  console.error(data.toString());
});
