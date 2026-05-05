/*
 * www.javagl.de - JSplat
 *
 * Copyright 2025 Marco Hutter - http://www.javagl.de
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package de.javagl.jsplat.processing;

import java.util.Arrays;

/**
 * Internal vector math utility functions.
 * 
 * <b>Note:</b> This class is currently public, but it is NOT part of the
 * public API!
 */
public class VecMath
{
    /**
     * Epsilon for quaternion computations
     */
    private static final double EPSILON = 1e-6;

    /**
     * Compute the scaling factors from the given 4x4 matrix.
     * 
     * The matrix is assumed to be a 16-element array representing a 4x4 matrix
     * in column-major order
     * 
     * @param matrix4 The matrix
     * @param result The result
     * @return The result
     */
    static double[] computeScales(double matrix4[], double result[])
    {
        double r[] = validate(result, 3);

        double m00 = matrix4[0];
        double m10 = matrix4[1];
        double m20 = matrix4[2];
        double m01 = matrix4[4];
        double m11 = matrix4[5];
        double m21 = matrix4[6];
        double m02 = matrix4[8];
        double m12 = matrix4[9];
        double m22 = matrix4[10];

        r[0] = Math.sqrt(m00 * m00 + m10 * m10 + m20 * m20);
        r[1] = Math.sqrt(m01 * m01 + m11 * m11 + m21 * m21);
        r[2] = Math.sqrt(m02 * m02 + m12 * m12 + m22 * m22);
        return r;
    }

    /**
     * Extract the rotation matrix as a 3x3 matrix from the 4x4 matrix
     * 
     * The matrix is assumed to be a 16-element array representing a 4x4 matrix
     * in column-major order
     * 
     * If the given result is <code>null</code>, a new array will be created and
     * returned.
     * 
     * @param matrix4 The matrix
     * @param scales The scaling factors, as computed with
     *        {@link #computeScales}
     * @param result The result
     * @return The result
     */
    static double[] extractRotation(double[] matrix4, double[] scales,
        double result[])
    {
        double r[] = validate(result, 9);

        r[0] = matrix4[0] / scales[0];
        r[1] = matrix4[1] / scales[0];
        r[2] = matrix4[2] / scales[0];
        r[3] = matrix4[4] / scales[1];
        r[4] = matrix4[5] / scales[1];
        r[5] = matrix4[6] / scales[1];
        r[6] = matrix4[8] / scales[2];
        r[7] = matrix4[9] / scales[2];
        r[8] = matrix4[10] / scales[2];
        return r;
    }

    /**
     * Convert the given rotation matrix into a scalar-last quaternion
     * 
     * The matrix is assumed to be a 9-element array representing a 3x3 matrix
     * in column-major order
     * 
     * If the given result is <code>null</code>, a new array will be created and
     * returned.
     * 
     * @param matrix3 The matrix
     * @param result The result
     * @return The result
     */
    static double[] rotationMatrixToScalarLastQuaternion(double[] matrix3,
        double result[])
    {
        double r[] = validate(result, 4);

        double m00 = matrix3[0];
        double m01 = matrix3[3];
        double m02 = matrix3[6];
        double m10 = matrix3[1];
        double m11 = matrix3[4];
        double m12 = matrix3[7];
        double m20 = matrix3[2];
        double m21 = matrix3[5];
        double m22 = matrix3[8];

        double trace = m00 + m11 + m22;
        if (trace > 0.0)
        {
            double S = Math.sqrt(trace + 1.0) * 2.0;
            r[0] = (m21 - m12) / S;
            r[1] = (m02 - m20) / S;
            r[2] = (m10 - m01) / S;
            r[3] = 0.25 * S;
            return r;
        }
        if ((m00 > m11) && (m00 > m22))
        {
            double S = Math.sqrt(1.0 + m00 - m11 - m22) * 2.0;
            r[0] = 0.25 * S;
            r[1] = (m01 + m10) / S;
            r[2] = (m02 + m20) / S;
            r[3] = (m21 - m12) / S;
            return r;
        }
        if (m11 > m22)
        {
            double S = Math.sqrt(1.0 + m11 - m00 - m22) * 2.0;
            r[0] = (m01 + m10) / S;
            r[1] = 0.25 * S;
            r[2] = (m12 + m21) / S;
            r[3] = (m02 - m20) / S;
            return r;
        }
        double S = Math.sqrt(1.0 + m22 - m00 - m11) * 2.0;
        r[0] = (m02 + m20) / S;
        r[1] = (m12 + m21) / S;
        r[2] = 0.25 * S;
        r[3] = (m10 - m01) / S;
        return r;
    }

    /**
     * Compute a 3x3 rotation matrix, as a 9-element array in column-major
     * order, from the given scalar-last quaternion.
     * 
     * If the given result is <code>null</code>, a new array will be created and
     * returned.
     * 
     * @param q The quaternion
     * @param result The result
     * @return The result
     */
    static double[] scalarLastQuaternionToRotationMatrix(double q[],
        double result[])
    {
        double r[] = validate(result, 9);

        double qx = q[0];
        double qy = q[1];
        double qz = q[2];
        double qw = q[3];

        double xx = qx * qx;
        double yy = qy * qy;
        double zz = qz * qz;
        double xy = qx * qy;
        double xz = qx * qz;
        double xw = qx * qw;
        double yz = qy * qz;
        double yw = qy * qw;
        double zw = qz * qw;

        r[0] = 1 - 2 * (yy + zz);
        r[1] = 2 * (xy + zw);
        r[2] = 2 * (xz - yw);

        r[3] = 2 * (xy - zw);
        r[4] = 1 - 2 * (xx + zz);
        r[5] = 2 * (yz + xw);

        r[6] = 2 * (xz + yw);
        r[7] = 2 * (yz - xw);
        r[8] = 1 - 2 * (xx + yy);
        return r;
    }

    /**
     * Multiply the given quaternions
     * 
     * If the given result is <code>null</code>, a new array will be created and
     * returned.
     * 
     * @param q0 The first quaternion
     * @param q1 The second quaternion
     * @param result The result
     * @return The result
     */
    static double[] multiplyScalarLastQuaternions(double[] q0, double[] q1,
        double result[])
    {
        double r[] = validate(result, 4);

        double q0x = q0[0];
        double q0y = q0[1];
        double q0z = q0[2];
        double q0w = q0[3];
        double q1x = q1[0];
        double q1y = q1[1];
        double q1z = q1[2];
        double q1w = q1[3];

        r[0] = q0w * q1x + q0x * q1w + q0y * q1z - q0z * q1y;
        r[1] = q0w * q1y - q0x * q1z + q0y * q1w + q0z * q1x;
        r[2] = q0w * q1z + q0x * q1y - q0y * q1x + q0z * q1w;
        r[3] = q0w * q1w - q0x * q1x - q0y * q1y - q0z * q1z;
        return r;
    }

    /**
     * Multiply the given 4x4 matrix with the given 3D point.
     * 
     * The matrix is assumed to be a 16-element array representing a 4x4 matrix
     * in column-major order
     * 
     * If the given result is <code>null</code>, a new array will be created and
     * returned.
     * 
     * @param matrix4 The matrix
     * @param point The 3D point
     * @param result The result
     * @return The result
     */
    static double[] multiplyMatrix4WithPoint(double matrix4[], double point[],
        double result[])
    {
        double r[] = validate(result, 3);

        double x = point[0];
        double y = point[1];
        double z = point[2];
        r[0] = matrix4[0] * x + matrix4[4] * y + matrix4[8] * z + matrix4[12];
        r[1] = matrix4[1] * x + matrix4[5] * y + matrix4[9] * z + matrix4[13];
        r[2] = matrix4[2] * x + matrix4[6] * y + matrix4[10] * z + matrix4[14];
        return r;
    }

    /**
     * Multiply the given 4x4 matrix with the given 3D vector.
     * 
     * The matrix is assumed to be a 16-element array representing a 4x4 matrix
     * in column-major order
     * 
     * If the given result is <code>null</code>, a new array will be created and
     * returned.
     * 
     * @param matrix4 The matrix
     * @param vector The 3D point
     * @param result The result
     * @return The result
     */
    static double[] multiplyMatrix4WithVector(double matrix4[], double vector[],
        double result[])
    {
        double r[] = validate(result, 3);

        double x = vector[0];
        double y = vector[1];
        double z = vector[2];
        r[0] = matrix4[0] * x + matrix4[4] * y + matrix4[8] * z;
        r[1] = matrix4[1] * x + matrix4[5] * y + matrix4[9] * z;
        r[2] = matrix4[2] * x + matrix4[6] * y + matrix4[10] * z;
        return r;
    }

    /**
     * Create a scalar-last quaternion that describes a rotation around the
     * given axis, about the given angle
     *
     * If the given result is <code>null</code>, a new array will be created and
     * returned.
     * 
     * @param axis The axis
     * @param angleRad The angle, in radians
     * @param result The result
     * @return The result
     */
    static double[] createScalarLastQuaternionFromAxisAngleRad(double axis[],
        double angleRad, double result[])
    {
        double r[] = validate(result, 4);

        double halfAngleRad = angleRad * 0.5;
        double s = Math.sin(halfAngleRad);

        double x = axis[0];
        double y = axis[1];
        double z = axis[2];
        double lenSquared = x * x + y * y + z * z;
        if (lenSquared < EPSILON)
        {
            r[0] = 0.0;
            r[1] = 0.0;
            r[2] = 0.0;
            r[3] = 1.0;
            return r;
        }

        double invLen = 1.0 / Math.sqrt(lenSquared);
        r[0] = x * invLen * s;
        r[1] = y * invLen * s;
        r[2] = z * invLen * s;
        r[3] = Math.cos(halfAngleRad);
        return r;
    }

    /**
     * Create an array containing the axis and the angle in radians that is
     * described by the given quaternion.
     *
     * If the given result is <code>null</code>, a new array will be created and
     * returned.
     * 
     * @param q The quaternion
     * @param result The result
     * @return The the result
     */
    static double[] createAxisAngleRadFromScalarLastQuaternion(double q[],
        double result[])
    {
        double r[] = validate(result, 4);

        double qx = q[0];
        double qy = q[1];
        double qz = q[2];
        double qw = q[3];
        double x = 1.0;
        double y = 0.0;
        double z = 0.0;
        double angleRad = 0.0;
        if (Math.abs(qw - 1.0) >= EPSILON && Math.abs(qw + 1.0) >= EPSILON)
        {
            double f = 1.0 / Math.sqrt(1.0 - qw * qw);
            x = qx * f;
            y = qy * f;
            z = qz * f;

        }
        if (Math.abs(qw - 1.0) >= EPSILON)
        {
            angleRad = 2.0 * Math.acos(qw);
        }
        r[0] = x;
        r[1] = y;
        r[2] = z;
        r[3] = angleRad;
        return r;
    }

    /**
     * Inverts the given matrix and writes the result into the given target
     * matrix. If the given matrix is not invertible, then the target matrix
     * will be set to identity.
     * 
     * The matrix is assumed to be a 16-element array representing a 4x4 matrix
     * in column-major order
     * 
     * If the given result is <code>null</code>, a new array will be created and
     * returned.
     * 
     * @param m The input matrix
     * @param result The inverse matrix
     * @return The result
     */
    static double[] invert4x4(double m[], double result[])
    {
        double r[] = validate(result, 16);

        // Adapted from The Mesa 3-D graphics library.
        // Copyright (C) 1999-2007 Brian Paul All Rights Reserved.
        // Published under the MIT license (see the header of this file)
        // @formatter:off
        double m0 = m[ 0];
        double m1 = m[ 1];
        double m2 = m[ 2];
        double m3 = m[ 3];
        double m4 = m[ 4];
        double m5 = m[ 5];
        double m6 = m[ 6];
        double m7 = m[ 7];
        double m8 = m[ 8];
        double m9 = m[ 9];
        double mA = m[10];
        double mB = m[11];
        double mC = m[12];
        double mD = m[13];
        double mE = m[14];
        double mF = m[15];

        r[ 0] =  m5 * mA * mF - m5 * mB * mE - m9 * m6 * mF + 
                 m9 * m7 * mE + mD * m6 * mB - mD * m7 * mA;
        r[ 4] = -m4 * mA * mF + m4 * mB * mE + m8 * m6 * mF - 
                 m8 * m7 * mE - mC * m6 * mB + mC * m7 * mA;
        r[ 8] =  m4 * m9 * mF - m4 * mB * mD - m8 * m5 * mF + 
                 m8 * m7 * mD + mC * m5 * mB - mC * m7 * m9;
        r[12] = -m4 * m9 * mE + m4 * mA * mD + m8 * m5 * mE - 
                 m8 * m6 * mD - mC * m5 * mA + mC * m6 * m9;
        r[ 1] = -m1 * mA * mF + m1 * mB * mE + m9 * m2 * mF - 
                 m9 * m3 * mE - mD * m2 * mB + mD * m3 * mA;
        r[ 5] =  m0 * mA * mF - m0 * mB * mE - m8 * m2 * mF + 
                 m8 * m3 * mE + mC * m2 * mB - mC * m3 * mA;
        r[ 9] = -m0 * m9 * mF + m0 * mB * mD + m8 * m1 * mF - 
                 m8 * m3 * mD - mC * m1 * mB + mC * m3 * m9;
        r[13] =  m0 * m9 * mE - m0 * mA * mD - m8 * m1 * mE + 
                 m8 * m2 * mD + mC * m1 * mA - mC * m2 * m9;
        r[ 2] =  m1 * m6 * mF - m1 * m7 * mE - m5 * m2 * mF + 
                 m5 * m3 * mE + mD * m2 * m7 - mD * m3 * m6;
        r[ 6] = -m0 * m6 * mF + m0 * m7 * mE + m4 * m2 * mF - 
                 m4 * m3 * mE - mC * m2 * m7 + mC * m3 * m6;
        r[10] =  m0 * m5 * mF - m0 * m7 * mD - m4 * m1 * mF + 
                 m4 * m3 * mD + mC * m1 * m7 - mC * m3 * m5;
        r[14] = -m0 * m5 * mE + m0 * m6 * mD + m4 * m1 * mE - 
                 m4 * m2 * mD - mC * m1 * m6 + mC * m2 * m5;
        r[ 3] = -m1 * m6 * mB + m1 * m7 * mA + m5 * m2 * mB - 
                 m5 * m3 * mA - m9 * m2 * m7 + m9 * m3 * m6;
        r[ 7] =  m0 * m6 * mB - m0 * m7 * mA - m4 * m2 * mB + 
                 m4 * m3 * mA + m8 * m2 * m7 - m8 * m3 * m6;
        r[11] = -m0 * m5 * mB + m0 * m7 * m9 + m4 * m1 * mB - 
                 m4 * m3 * m9 - m8 * m1 * m7 + m8 * m3 * m5;
        r[15] =  m0 * m5 * mA - m0 * m6 * m9 - m4 * m1 * mA + 
                 m4 * m2 * m9 + m8 * m1 * m6 - m8 * m2 * m5;
        // (Ain't that pretty?)
        // @formatter:on

        double det = m0 * r[0] + m1 * r[4] + m2 * r[8] + m3 * r[12];
        if (Math.abs(det) <= EPSILON)
        {
            identity4x4(r);
            return r;
        }
        double invDet = 1.0 / det;
        for (int i = 0; i < 16; i++)
        {
            r[i] *= invDet;
        }
        return r;
    }

    /**
     * Fills the given result matrix with the product of the given matrices.
     * 
     * The matrices are assumed to be a 16-element arrays representing 4x4
     * matrices in column-major order
     * 
     * If the given result is <code>null</code>, a new array will be created and
     * returned.
     * 
     * @param a The first matrix
     * @param b The second matrix
     * @param result The result
     * @return The result
     */
    public static double[] mul4x4(double a[], double b[], double result[])
    {
        double r[] = validate(result, 16);

        double a00 = a[0];
        double a10 = a[1];
        double a20 = a[2];
        double a30 = a[3];
        double a01 = a[4];
        double a11 = a[5];
        double a21 = a[6];
        double a31 = a[7];
        double a02 = a[8];
        double a12 = a[9];
        double a22 = a[10];
        double a32 = a[11];
        double a03 = a[12];
        double a13 = a[13];
        double a23 = a[14];
        double a33 = a[15];

        double b00 = b[0];
        double b10 = b[1];
        double b20 = b[2];
        double b30 = b[3];
        double b01 = b[4];
        double b11 = b[5];
        double b21 = b[6];
        double b31 = b[7];
        double b02 = b[8];
        double b12 = b[9];
        double b22 = b[10];
        double b32 = b[11];
        double b03 = b[12];
        double b13 = b[13];
        double b23 = b[14];
        double b33 = b[15];

        double m00 = a00 * b00 + a01 * b10 + a02 * b20 + a03 * b30;
        double m01 = a00 * b01 + a01 * b11 + a02 * b21 + a03 * b31;
        double m02 = a00 * b02 + a01 * b12 + a02 * b22 + a03 * b32;
        double m03 = a00 * b03 + a01 * b13 + a02 * b23 + a03 * b33;

        double m10 = a10 * b00 + a11 * b10 + a12 * b20 + a13 * b30;
        double m11 = a10 * b01 + a11 * b11 + a12 * b21 + a13 * b31;
        double m12 = a10 * b02 + a11 * b12 + a12 * b22 + a13 * b32;
        double m13 = a10 * b03 + a11 * b13 + a12 * b23 + a13 * b33;

        double m20 = a20 * b00 + a21 * b10 + a22 * b20 + a23 * b30;
        double m21 = a20 * b01 + a21 * b11 + a22 * b21 + a23 * b31;
        double m22 = a20 * b02 + a21 * b12 + a22 * b22 + a23 * b32;
        double m23 = a20 * b03 + a21 * b13 + a22 * b23 + a23 * b33;

        double m30 = a30 * b00 + a31 * b10 + a32 * b20 + a33 * b30;
        double m31 = a30 * b01 + a31 * b11 + a32 * b21 + a33 * b31;
        double m32 = a30 * b02 + a31 * b12 + a32 * b22 + a33 * b32;
        double m33 = a30 * b03 + a31 * b13 + a32 * b23 + a33 * b33;

        r[0] = m00;
        r[1] = m10;
        r[2] = m20;
        r[3] = m30;
        r[4] = m01;
        r[5] = m11;
        r[6] = m21;
        r[7] = m31;
        r[8] = m02;
        r[9] = m12;
        r[10] = m22;
        r[11] = m32;
        r[12] = m03;
        r[13] = m13;
        r[14] = m23;
        r[15] = m33;
        return r;
    }

    /**
     * Create a 4x4 matrix from the given 3x3 matrix.
     * 
     * The matrix is assumed to be a 9-element array representing a 3x3 matrix
     * in column-major order
     * 
     * If the given result is <code>null</code>, a new array will be created and
     * returned.
     * 
     * @param matrix3 The matrix
     * @param result The result
     * @return The result
     */
    static double[] createMatrix4FromMatrix3(double matrix3[], double result[])
    {
        double r[] = validate(result, 16);

        r[0] = matrix3[0];
        r[1] = matrix3[1];
        r[2] = matrix3[2];

        r[4] = matrix3[3];
        r[5] = matrix3[4];
        r[6] = matrix3[5];

        r[8] = matrix3[6];
        r[9] = matrix3[7];
        r[10] = matrix3[8];

        r[15] = 1.0;

        return r;

    }

    /**
     * Set the given matrix to be the identity matrix.
     * 
     * If the given result is <code>null</code>, a new array will be created and
     * returned.
     * 
     * 
     * @param result The result
     * @return The result
     */
    public static double[] identity4x4(double result[])
    {
        double r[] = validate(result, 16);

        Arrays.fill(r, 0.0);
        r[0] = 1.0;
        r[5] = 1.0;
        r[10] = 1.0;
        r[15] = 1.0;
        return r;
    }

    /**
     * Writes the given matrix into the given result matrix, with the given
     * values added to the translation component
     * 
     * The matrix is assumed to be a 16-element array representing a 4x4 matrix
     * in column-major order
     * 
     * If the given result is <code>null</code>, a new array will be created and
     * returned.
     * 
     * @param m The input matrix
     * @param x The x-translation
     * @param y The y-translation
     * @param z The z-translation
     * @param result The result
     * @return The result
     */
    public static double[] translate4x4(double m[], double x, double y, double z,
        double result[])
    {
        double r[] = validate(result, 16);
        set(m, r);
        r[12] += x;
        r[13] += y;
        r[14] += z;
        return r;
    }

    
    /**
     * Create a matrix describing a rotation around the x-axis, and the given
     * translation
     * 
     * If the given result is <code>null</code>, a new array will be created and
     * returned.
     * 
     * @param angleRad The angle, in radians
     * @param result The result
     * @return The matrix
     */
    public static double[] rotationX(double angleRad, double result[])
    {
        double matrix[] = validate(result, 16);

        double c = Math.cos(angleRad);
        double s = Math.sin(angleRad);

        // Column 0
        matrix[0] = 1.0;
        matrix[1] = 0.0;
        matrix[2] = 0.0;
        matrix[3] = 0.0;

        // Column 1
        matrix[4] = 0.0;
        matrix[5] = c;
        matrix[6] = s;
        matrix[7] = 0.0;

        // Column 2
        matrix[8] = 0.0;
        matrix[9] = -s;
        matrix[10] = c;
        matrix[11] = 0.0;

        // Column 3
        matrix[12] = 0.0;
        matrix[13] = 0.0;
        matrix[14] = 0.0;
        matrix[15] = 1.0;

        return matrix;
    }

    /**
     * Create a matrix describing a rotation around the y-axis
     * 
     * If the given result is <code>null</code>, a new array will be created and
     * returned.
     * 
     * @param angleRad The angle, in radians
     * @param result The result
     * @return The matrix
     */
    public static double[] rotationY(double angleRad, double result[])
    {
        double matrix[] = validate(result, 16);

        double c = Math.cos(angleRad);
        double s = Math.sin(angleRad);

        // Column 0
        matrix[0] = c;
        matrix[1] = 0.0;
        matrix[2] = -s;
        matrix[3] = 0.0;

        // Column 1
        matrix[4] = 0.0;
        matrix[5] = 1.0;
        matrix[6] = 0.0;
        matrix[7] = 0.0;

        // Column 2
        matrix[8] = s;
        matrix[9] = 0.0;
        matrix[10] = c;
        matrix[11] = 0.0;

        // Column 3
        matrix[12] = 0.0;
        matrix[13] = 0.0;
        matrix[14] = 0.0;
        matrix[15] = 1.0;

        return matrix;
    }
    
    
    /**
     * Create a matrix describing a rotation around the z-axis
     * 
     * If the given result is <code>null</code>, a new array will be created and
     * returned.
     * 
     * @param angleRad The angle, in radians
     * @param result The result
     * @return The matrix
     */
    public static double[] rotationZ(double angleRad, double result[])
    {
        double matrix[] = validate(result, 16);

        double c = Math.cos(angleRad);
        double s = Math.sin(angleRad);

        // Column 0
        matrix[0] = c;
        matrix[1] = s;
        matrix[2] = 0.0;
        matrix[3] = 0.0;

        // Column 1
        matrix[4] = -s;
        matrix[5] = c;
        matrix[6] = 0.0;
        matrix[7] = 0.0;

        // Column 2
        matrix[8] = 0.0;
        matrix[9] = 0.0;
        matrix[10] = 1.0;
        matrix[11] = 0.0;

        // Column 3
        matrix[12] = 0.0;
        matrix[13] = 0.0;
        matrix[14] = 0.0;
        matrix[15] = 1.0;

        return matrix;
    }
    
    /**
     * Create a matrix with a uniform scale.
     * 
     * If the given result is <code>null</code>, a new array will be created and
     * returned.
     * 
     * @param s The scaling
     * @param result The result 
     * @return The result
     */
    public static double[] scale4x4(double s, double result[])
    {
        double[] r = identity4x4(result);
        r[0] = s;
        r[5] = s;
        r[10] = s;
        r[15] = 1.0;
        return r;
    }
    
    /**
     * Create a matrix with a given scale.
     * 
     * If the given result is <code>null</code>, a new array will be created and
     * returned.
     * 
     * @param sx The scaling along x
     * @param sy The scaling along y
     * @param sz The scaling along z
     * @param result The result 
     * @return The result
     */
    public static double[] scale4x4(double sx, double sy, double sz, double result[])
    {
        double[] r = identity4x4(result);
        r[0] = sx;
        r[5] = sy;
        r[10] = sz;
        r[15] = 1.0;
        return r;
    }
    
    /**
     * Copy the contents of the source array to the given target array. The
     * length of the shorter array will determine how many elements are copied.
     * 
     * @param source The source array
     * @param target The target array
     */
    private static void set(double source[], double target[])
    {
        System.arraycopy(source, 0, target, 0,
            Math.min(source.length, target.length));
    }

    /**
     * Ensure that the given array is not <code>null</code> and has the given
     * length, returning a new array with the given length if this is not the
     * case
     * 
     * @param array The array
     * @param length The length
     * @return The result
     */
    private static double[] validate(double array[], int length)
    {
        if (array == null)
        {
            return new double[length];
        }
        if (array.length != length)
        {
            return new double[length];
        }
        return array;
    }

    /**
     * Private constructor to prevent instantiation
     */
    private VecMath()
    {
        // Private constructor to prevent instantiation
    }

}
