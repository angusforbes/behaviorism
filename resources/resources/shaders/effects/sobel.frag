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
const int MaxKernelSize = 122; //225;

// array of offsets for accessing the base image
uniform vec2 Offset[MaxKernelSize];

// size of kernel (width * height) for this execution
uniform int KernelSize;

// value for each location in the convolution kernel
uniform vec4 KernelValueH[MaxKernelSize];
uniform vec4 KernelValueV[MaxKernelSize];

// image to be convolved
uniform sampler2D BaseImage;

uniform vec4 ScaleFactor;

void main()
{
    int i;
    vec4 sum1 = vec4(0.0);
    vec4 sum2 = vec4(0.0);

    for (i = 0; i < KernelSize; i++)
    {
        vec4 tmp1 = texture2D(BaseImage, gl_TexCoord[0].st + Offset[i].xy);
        sum1 += tmp1 * KernelValueH[i];
        vec4 tmp2 = texture2D(BaseImage, gl_TexCoord[0].st + Offset[i].xy);
        sum2 += tmp2 * KernelValueV[i];
    }

    vec4 sum = sqrt( (sum1 * sum1) + (sum2 * sum2) );
    //sum = texture2D(BaseImage, gl_TexCoord[0].st + Offset[25]) * KernelValue[25];
    vec4 bah = ScaleFactor;
    //gl_FragColor = sum;
    gl_FragColor = sum * ScaleFactor;
    gl_FragColor.a = 1.0;

    //gl_FragColor = texture2D(BaseImage, gl_TexCoord[0].st).aaaa;
    //gl_FragColor = texture2D(BaseImage, gl_TexCoord[0].st);
    //gl_FragColor = vec4(1, 0, 0, 1);
}