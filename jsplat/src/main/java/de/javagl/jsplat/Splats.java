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
package de.javagl.jsplat;

import java.util.List;

/**
 * Methods related to splats
 */
public class Splats
{
    /**
     * The first spherical harmonics coefficient
     */
    private static final double SH_C0 = Math.sqrt(1.0 / Math.PI) * 0.5;

    /**
     * Create a new default splat with the given spherical harmonics degree
     * 
     * @param shDegree The spherical harmonics degree
     * @return The {@link MutableSplat}
     */
    public static MutableSplat create(int shDegree)
    {
        return new DefaultSplat(shDegree);
    }

    /**
     * Create an unspecified string representation of the given {@link Splat}.
     * 
     * This is mainly intended for debugging. The representation may change at
     * any point in time.
     * 
     * @param splat The {@link Splat}
     * @return The string representation
     */
    public static String createString(Splat splat)
    {
        StringBuilder sb = new StringBuilder();

        float px = splat.getPositionX();
        float py = splat.getPositionY();
        float pz = splat.getPositionZ();

        float sx = splat.getScaleX();
        float sy = splat.getScaleY();
        float sz = splat.getScaleZ();

        float rx = splat.getRotationX();
        float ry = splat.getRotationY();
        float rz = splat.getRotationZ();
        float rw = splat.getRotationW();

        float o = splat.getOpacity();

        String separator = "\n";

        String ps = "position=(" + px + "," + py + "," + pz + ")";
        String ss = "scale=(" + sx + "," + sy + "," + sz + ")";
        String rs = "rotation=(" + rx + "," + ry + "," + rz + "," + rw + ")";
        String os = "opacity=" + o;

        sb.append(ps).append(separator);
        sb.append(ss).append(separator);
        sb.append(rs).append(separator);
        sb.append(os).append(separator);

        int dimensions = splat.getShDimensions();
        for (int d = 0; d < dimensions; d++)
        {
            float shx = splat.getShX(d);
            float shy = splat.getShY(d);
            float shz = splat.getShZ(d);
            String shs = "sh" + d + "=(" + shx + "," + shy + "," + shz + ")";
            sb.append(shs).append(separator);
        }

        return sb.toString();
    }

    /**
     * Set the values of the given {@link Splat} in the given
     * {@link MutableSplat}.
     * 
     * @param s The source {@link Splat}
     * @param t The target {@link MutableSplat}
     * @throws IllegalArgumentException If the spherical harmonics degrees of
     *         the source and target are different
     */
    public static void set(Splat s, MutableSplat t)
    {
        if (s.getShDegree() != t.getShDegree())
        {
            throw new IllegalArgumentException(
                "Cannot assign the values from a splat with spherical "
                    + "harmonics degree " + s.getShDegree()
                    + " to a splat with spherical harmonics degree "
                    + t.getShDegree());
        }
        setAny(s, t);
    }

    /**
     * Set the values of the given {@link Splat} in the given
     * {@link MutableSplat}.
     * 
     * If the source has a higher number of dimensions than the target, then the
     * dimensions that exceed that of the target will be omitted.
     *
     * If the target has a higher number of dimensions than the source, then the
     * remaining spherical harmonics in the target will be set to 0.0.
     * 
     * @param s The source {@link Splat}
     * @param t The target {@link MutableSplat}
     */
    public static void setAny(Splat s, MutableSplat t)
    {
        t.setPositionX(s.getPositionX());
        t.setPositionY(s.getPositionY());
        t.setPositionZ(s.getPositionZ());

        t.setScaleX(s.getScaleX());
        t.setScaleY(s.getScaleY());
        t.setScaleZ(s.getScaleZ());

        t.setRotationX(s.getRotationX());
        t.setRotationY(s.getRotationY());
        t.setRotationZ(s.getRotationZ());
        t.setRotationW(s.getRotationW());

        t.setOpacity(s.getOpacity());

        int sDimensions = s.getShDimensions();
        int tDimensions = t.getShDimensions();
        int dimensions = Math.min(sDimensions, tDimensions);
        for (int i = 0; i < dimensions; i++)
        {
            t.setShX(i, s.getShX(i));
            t.setShY(i, s.getShY(i));
            t.setShZ(i, s.getShZ(i));
        }
        if (tDimensions > sDimensions)
        {
            for (int i = sDimensions; i < tDimensions; i++)
            {
                t.setShX(i, 0.0f);
                t.setShY(i, 0.0f);
                t.setShZ(i, 0.0f);
            }
        }
    }

    /**
     * Converts given the first-order spherical harmonics coefficient (usually
     * stored to as <code>"f_dc"</code>, e.g. in PLY files) into a color value
     * in [0, 1].
     * 
     * @param dc The coefficient
     * @return The color value
     */
    public static float directCurrentToColor(float dc)
    {
        float f = (float) (0.5 + SH_C0 * dc);
        return clamp(f);
    }

    /**
     * Converts given color value in [0, 1] into a first-order spherical
     * harmonics coefficient (usually stored to as <code>"f_dc"</code>, e.g. in
     * PLY files)
     * 
     * @param c The color value
     * @return The coefficient
     */
    public static float colorToDirectCurrent(float c)
    {
        float dc = (float) ((c - 0.5) / SH_C0);
        return dc;
    }

    /**
     * Converts the given opacity value from the range [-Inf,Inf] into an alpha
     * value in the range [0, 1].
     * 
     * @param v The opacity
     * @return The alpha value
     */
    public static float opacityToAlpha(float v)
    {
        float alpha = (float) (1.0 / (1.0 + Math.exp(-v)));
        return clamp(alpha);
    }

    /**
     * Converts the given alpha value from the range [0, 1] into an opacity
     * value in range [-Inf,Inf].
     * 
     * @param a The alpha value
     * @return The opacity value
     */
    public static float alphaToOpacity(float a)
    {
        // This is to avoid infinity, but pick a value that
        // results in 255 or 0 during the inverse operation
        if (a == 1.0f)
        {
            return 20.0f;
        }
        if (a == 0.0f)
        {
            return -20.0f;
        }
        float opacity = (float) -Math.log(1.0f / a - 1.0);
        return opacity;
    }

    /**
     * Clamp the given value to [0, 1]
     * 
     * @param f The value
     * @return The result
     */
    private static float clamp(float f)
    {
        if (f < 0.0f)
        {
            return 0.0f;
        }
        if (f > 1.0f)
        {
            return 1.0f;
        }
        return f;
    }

    /**
     * Returns the total number of dimensions for the given spherical harmonics
     * degree.
     * 
     * @param degree The degree
     * @return The dimensions
     */
    public static int dimensionsForDegree(int degree)
    {
        return (degree + 1) * (degree + 1);
    }

    /**
     * Returns the number of spherical harmonics coefficients for the given
     * degree.
     * 
     * @param degree The degree
     * @return The number of coefficients
     */
    public static int coefficientsForDegree(int degree)
    {
        return degree * 2 + 1;
    }

    /**
     * Returns the index of the dimension for the specified spherical harmonics
     * coefficient.
     * 
     * <pre><code>
     * (degree 0, coefficient 0) : 0
     * 
     * (degree 1, coefficient 0) : 1
     * (degree 1, coefficient 1) : 2
     * (degree 1, coefficient 2) : 3
     * 
     * (degree 2, coefficient 0) : 4
     * (degree 2, coefficient 1) : 5
     * (degree 2, coefficient 2) : 6
     * (degree 2, coefficient 3) : 7
     * (degree 2, coefficient 4) : 8
     * 
     * (degree 3, coefficient 0) : 9
     * ...
     * (degree 3, coefficient 6) : 15
     * </code></pre>
     * 
     * @param degree The degree
     * @param coefficient The coefficient
     * @return The dimension
     */
    public static int dimensionForCoefficient(int degree, int coefficient)
    {
        int index = 0;
        for (int d = 0; d < degree; d++)
        {
            int dd = Splats.coefficientsForDegree(d);
            index += dd;
        }
        index += coefficient;
        return index;
    }

    /**
     * Returns whether the given lists of splats are epsilon-equal
     * 
     * @param sas The first list
     * @param sbs The second list
     * @param epsilon The epsilon
     * @return The result
     */
    public static boolean equalsEpsilon(List<? extends Splat> sas,
        List<? extends Splat> sbs, float epsilon)
    {
        if (sas.size() != sbs.size())
        {
            return false;
        }
        if (sas.size() == 0)
        {
            return true;
        }
        for (int i = 0; i < sas.size(); i++)
        {
            Splat sa = sas.get(i);
            Splat sb = sbs.get(i);
            if (!equalsEpsilon(sa, sb, epsilon))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns whether the given splats are epsilon-equal.
     * 
     * This involves special treatment for the scalar (rotation) component of
     * the quaternions: It will treat them as actual rotation angles, meaning
     * that values like 1.0 and -1.0 will be considered to be equal.
     * 
     * @param sa The first splat
     * @param sb The second splat
     * @param epsilon The epsilon
     * @return The result
     */
    public static boolean equalsEpsilon(Splat sa, Splat sb, float epsilon)
    {
        int dimensionsA = sa.getShDimensions();
        int dimensionsB = sb.getShDimensions();
        if (dimensionsA != dimensionsB)
        {
            return false;
        }

        // Position
        if (!equalsEpsilon(sa.getPositionX(), sb.getPositionX(), epsilon))
        {
            return false;
        }
        if (!equalsEpsilon(sa.getPositionY(), sb.getPositionY(), epsilon))
        {
            return false;
        }
        if (!equalsEpsilon(sa.getPositionZ(), sb.getPositionZ(), epsilon))
        {
            return false;
        }

        // Scale
        if (!equalsEpsilon(sa.getScaleX(), sb.getScaleX(), epsilon))
        {
            return false;
        }
        if (!equalsEpsilon(sa.getScaleY(), sb.getScaleY(), epsilon))
        {
            return false;
        }
        if (!equalsEpsilon(sa.getScaleZ(), sb.getScaleZ(), epsilon))
        {
            return false;
        }

        // Rotation
        if (!equalsEpsilon(sa.getRotationX(), sb.getRotationX(), epsilon))
        {
            return false;
        }
        if (!equalsEpsilon(sa.getRotationY(), sb.getRotationY(), epsilon))
        {
            return false;
        }
        if (!equalsEpsilon(sa.getRotationZ(), sb.getRotationZ(), epsilon))
        {
            return false;
        }

        // Special treatment for rotation component: The difference
        // modulo 1.0 is computed, and should be epsilon-equal to 0
        float wa = sa.getRotationW();
        float wb = sb.getRotationW();
        float wd = 1.0f - Math.abs(Math.abs(wa - wb) - 1.0f);
        if (!equalsEpsilon(wd, 0.0f, epsilon))
        {
            return false;
        }

        // Opacity
        if (!equalsEpsilon(sa.getOpacity(), sb.getOpacity(), epsilon))
        {
            return false;
        }

        // Spherical harmonics
        for (int d = 0; d < dimensionsA; d++)
        {
            if (!equalsEpsilon(sa.getShX(d), sb.getShX(d), epsilon))
            {
                return false;
            }
            if (!equalsEpsilon(sa.getShY(d), sb.getShY(d), epsilon))
            {
                return false;
            }
            if (!equalsEpsilon(sa.getShZ(d), sb.getShZ(d), epsilon))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns whether the given splats are strictly epsilon-equal.
     * 
     * This means that it will compare the actual values of the splats,
     * regardless of their semantics. For rotation quaternions, a scalar value
     * of 1.0 and -1.0 describe the same rotation, and this will <b>not</b> be
     * taken into account here.
     * 
     * @param sa The first splat
     * @param sb The second splat
     * @param epsilon The epsilon
     * @return The result
     */
    public static boolean strictEqualsEpsilon(Splat sa, Splat sb, float epsilon)
    {
        int dimensionsA = sa.getShDimensions();
        int dimensionsB = sb.getShDimensions();
        if (dimensionsA != dimensionsB)
        {
            return false;
        }

        // Position
        if (!equalsEpsilon(sa.getPositionX(), sb.getPositionX(), epsilon))
        {
            return false;
        }
        if (!equalsEpsilon(sa.getPositionY(), sb.getPositionY(), epsilon))
        {
            return false;
        }
        if (!equalsEpsilon(sa.getPositionZ(), sb.getPositionZ(), epsilon))
        {
            return false;
        }

        // Scale
        if (!equalsEpsilon(sa.getScaleX(), sb.getScaleX(), epsilon))
        {
            return false;
        }
        if (!equalsEpsilon(sa.getScaleY(), sb.getScaleY(), epsilon))
        {
            return false;
        }
        if (!equalsEpsilon(sa.getScaleZ(), sb.getScaleZ(), epsilon))
        {
            return false;
        }

        // Rotation
        if (!equalsEpsilon(sa.getRotationX(), sb.getRotationX(), epsilon))
        {
            return false;
        }
        if (!equalsEpsilon(sa.getRotationY(), sb.getRotationY(), epsilon))
        {
            return false;
        }
        if (!equalsEpsilon(sa.getRotationZ(), sb.getRotationZ(), epsilon))
        {
            return false;
        }
        if (!equalsEpsilon(sa.getRotationW(), sb.getRotationW(), epsilon))
        {
            return false;
        }

        // Opacity
        if (!equalsEpsilon(sa.getOpacity(), sb.getOpacity(), epsilon))
        {
            return false;
        }

        // Spherical harmonics
        for (int d = 0; d < dimensionsA; d++)
        {
            if (!equalsEpsilon(sa.getShX(d), sb.getShX(d), epsilon))
            {
                return false;
            }
            if (!equalsEpsilon(sa.getShY(d), sb.getShY(d), epsilon))
            {
                return false;
            }
            if (!equalsEpsilon(sa.getShZ(d), sb.getShZ(d), epsilon))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns whether the given values are epsilon-equal
     * 
     * @param a The first value
     * @param b The second value
     * @param epsilon The relative epsilon
     * @return Whether they are equal
     */
    private static boolean equalsEpsilon(float a, float b, float epsilon)
    {
        float d = Math.abs(a - b);
        if (d < epsilon)
        {
            return true;
        }
        float aa = Math.abs(a);
        float ab = Math.abs(b);
        if (aa < ab)
        {
            return d <= ab * epsilon;
        }
        return d <= aa * epsilon;
    }

    /**
     * Private constructor to prevent instantiation
     */
    private Splats()
    {
        // Private constructor to prevent instantiation
    }

}