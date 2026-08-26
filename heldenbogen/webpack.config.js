const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const path = require('path');

module.exports = {
    // entry: "./index.js",
    cache: false,
    entry: "./index.js",
    mode: "development",
    output: {
        path: path.resolve(__dirname, "../build/resources/main/heldenbogen"),
    },
    plugins: [new MiniCssExtractPlugin()],
    module: {
        rules: [
            {
                test: /\.s[ac]ss$/i,
                use: [
                    MiniCssExtractPlugin.loader,
                    // Creates `style` nodes from JS strings
                    // "style-loader",
                    // Translates CSS into CommonJS
                    "css-loader",
                    // Compiles Sass to CSS
                    "sass-loader",
                ],
            },
            {
                test: /\.js$/i,
                use: [
                    'babel-loader'
                ]
            },
        ],
    },
};