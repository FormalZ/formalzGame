var webpackConfig = require("./webpack.dev.config");
const webpack = require('webpack');

module.exports = function(config) {
    config.set({
        basePath: "",
        frameworks: ["mocha", "chai", "sinon", "source-map-support"],
        logLevel: config.LOG_NONE,
        files: [
            //"src/**/*.ts",
            //"node_modules/phaser-ce/build/phaser.js",
            "test/unit/*.ts"
        ],
        captureTimeout: 210000,
        browserDisconnectTolerance: 3, 
        browserDisconnectTimeout : 210000,
        browserNoActivityTimeout : 210000,
        browsers: ['ChromeHeadlessNoSandbox'],
            customLaunchers: {
            ChromeHeadlessNoSandbox: {
                base: 'ChromeHeadless',
                flags: ['--no-sandbox']
            }
        },
        exclude: [
            "src/globals.d.ts",
            "src/utils/*.ts",
            "src/assets.ts",
        ],
        preprocessors: {
            "src/**/*.ts": ["webpack"],
            "test/unit/*.ts": ["webpack"]
        },
        webpack: {
            module: webpackConfig.module,
            resolve: webpackConfig.resolve,
            plugins: [
                new webpack.DefinePlugin({
                    'DEBUG': true,
                    // Do not modify these manually, you may break things...
                    'DEFAULT_GAME_WIDTH': /*[[DEFAULT_GAME_WIDTH*/800/*DEFAULT_GAME_WIDTH]]*/,
                    'DEFAULT_GAME_HEIGHT': /*[[DEFAULT_GAME_HEIGHT*/500/*DEFAULT_GAME_HEIGHT]]*/,
                    'MAX_GAME_WIDTH': /*[[MAX_GAME_WIDTH*/888/*MAX_GAME_WIDTH]]*/,
                    'MAX_GAME_HEIGHT': /*[[MAX_GAME_HEIGHT*/600/*MAX_GAME_HEIGHT]]*/,
                    'SCALE_MODE': JSON.stringify(/*[[SCALE_MODE*/'USER_SCALE'/*SCALE_MODE]]*/),
                    // The items below are most likely the ones we should be adjusting
                    // Add or remove entries in this array to change which fonts are loaded
                    'GOOGLE_WEB_FONTS': JSON.stringify(['Barrio']),
                    // Re-order the items in this array to change the desired order of checking your audio sources (do not add/remove/modify the entries themselves)
                    'SOUND_EXTENSIONS_PREFERENCE': JSON.stringify(['webm', 'ogg', 'm4a', 'mp3', 'aac', 'ac3', 'caf', 'flac', 'mp4', 'wav'])
                }),
                // new webpack.SourceMapDevToolPlugin({
                //     filename: null, // if no value is provided the sourcemap is inlined
                //     test: /\.(ts|js)($|\?)/i // process .js and .ts files only
                //   })
            ],
            watch: true
        },
        webpackMiddleware: {
            noInfo: true, // <-- doesn't work with webpack 3? 
            // stats: "errors-only" // <--- remove this line to see the compile progressles: false
        },
        reporters: ["mocha", 'coverage', 'karma-remap-istanbul'],
        port: 9876,
        colors: true,
        autoWatch: false,
        mime: {
            "text/x-typescript": ["ts", "tsx"]
        },
        client: {
            captureConsole: true,
            mocha: {
              bail: true
            }
        },
        coverageReporter: {
            type : 'in-memory',
        },
        remapIstanbulReporter: {
            reports: {
              html: 'test/coverage'
            }
        },
        singleRun: true,
        concurrency: Infinity,
        node: {
            fs: 'empty',
        },
    });
};