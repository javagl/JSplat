/*
 * www.javagl.de - JSplat
 *
 * Copyright 2025 Marco Hutter - http://www.javagl.de
 */
// This is extracted from
// https://github.com/limacv/GaussianSplattingViewer
// Commit: 2492259fd7db3168509395728660b5f978de5db2
// Original license statement:
/*
MIT License

Copyright (c) 2023 Li Ma

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

#version 430 core

in vec3 color;
in float alpha;
in vec3 conic;
in vec2 coordxy;  // local coordinate in quad, unit in pixel

uniform int render_mod;  // > 0 render 0-ith SH dim, -1 depth, -2 bill board, -3 flat ball, -4 gaussian ball

out vec4 FragColor;

void main()
{
    if (render_mod == -2)
    {
        FragColor = vec4(color, 1.f);
        return;
    }

    float power = -0.5f * (conic.x * coordxy.x * coordxy.x + conic.z * coordxy.y * coordxy.y) - conic.y * coordxy.x * coordxy.y;
    if (power > 0.f)
        discard;
    float opacity = min(0.99f, alpha * exp(power));
    if (opacity < 1.f / 255.f)
        discard;
    FragColor = vec4(color, opacity);

    // handling special shading effect
    if (render_mod == -3)
        FragColor.a = FragColor.a > 0.22 ? 1 : 0;
    else if (render_mod == -4)
    {
        FragColor.a = FragColor.a > 0.22 ? 1 : 0;
        FragColor.rgb = FragColor.rgb * exp(power);
    }
}

