cd ../../Front-end
npm run build:dist
cp -v -f ./dist/game.min.js ../public_html/impress/public/js
rm -rf ../public_html/impress/public/assets
cp -v -R ./dist/assets ../public_html/impress/public
