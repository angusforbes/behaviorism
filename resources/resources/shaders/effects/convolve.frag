//
// fragment shader for general convolution
//
// Author: Randi Rost
//
// Copyright (c) 2003-2005: 3Dlabs, Inc.
//
// See 3Dlabs-License.txt for license information
//

// maximum size supported by this shader
const int MaxKernelSize = 201;

// array of offsets for accessing the base image
uniform vec2 Offset[MaxKernelSize];

// size of kernel (width * height) for this execution
uniform int KernelSize;

// value for each location in the convolution kernel
uniform vec4 KernelValue[MaxKernelSize];

// image to be convolved
uniform sampler2D BaseImage;

uniform vec4 ScaleFactor;

void main()
{
    int i;
    vec4 sum = vec4(0.0);

    for (i = 0; i < KernelSize; i++)
    {
        vec4 tmp = texture2D(BaseImage, gl_TexCoord[0].st + Offset[i].xy);
        sum += tmp * KernelValue[i];
    }

    //sum = texture2D(BaseImage, gl_TexCoord[0].st + Offset[25]) * KernelValue[25];
    vec4 bah = ScaleFactor;
    //gl_FragColor = sum;
    gl_FragColor = (sum * ScaleFactor);
    gl_FragColor.a = 1.0;

    //gl_FragColor = texture2D(BaseImage, gl_TexCoord[0].st).aaaa;
    //gl_FragColor = texture2D(BaseImage, gl_TexCoord[0].st);
    //gl_FragColor = vec4(1, 0, 0, 1);
}