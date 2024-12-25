package сom.cgvsu.test;

import com.cgvsu.affine.*;
import com.cgvsu.math.Vector3f;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AffineTransformTest {
    @Test
    public void testScale() {
        Vector3f expectedVec = new Vector3f(2, 3, 5);

        AffineTransform affineTransformation = new Scaling(2, 3, 5);
        Vector3f vec = new Vector3f(1, 1, 1);

        Assertions.assertTrue(expectedVec.equals(affineTransformation.transform(vec)));
    }

    @Test
    public void testTranslate() {
        Vector3f expectedVec = new Vector3f(9, -5, 2);

        AffineTransform affineTransformation = new Translator(9, -5, 2);
        Vector3f vec = new Vector3f(0, 0, 0);

        Assertions.assertTrue(expectedVec.equals(affineTransformation.transform(vec)));
    }

    @Test
    public void testDefaultTransform() {
        Vector3f expectedVec = new Vector3f(9, -5, 2);

        AffineTransform affineTransformation = new Transformation();
        Vector3f vec = new Vector3f(9, -5, 2);

        Assertions.assertTrue(expectedVec.equals(affineTransformation.transform(vec)));
    }

    @Test
    public void testScaleTransform() {
        Vector3f expectedVec = new Vector3f(9, -5, 2);

        AffineTransform affineTransformation = new Transformation(new Scaling(9, -5, 2));
        Vector3f vec = new Vector3f(1, 1, 1);

        Assertions.assertTrue(expectedVec.equals(affineTransformation.transform(vec)));
    }

    @Test
    public void testRotateTransform() {
        Vector3f expectedVec = new Vector3f(1, -1, 1);

        AffineTransform affineTransformation = new Transformation(
                new Rotator(90, Rotator.Axis.X),
                new Rotator(90, Rotator.Axis.Y),
                new Rotator(90, Rotator.Axis.Z));
        Vector3f vec = new Vector3f(1, 1, 1);

        Assertions.assertTrue(expectedVec.equals(affineTransformation.transform(vec)));
    }

    @Test
    public void testTranslateTransform() {
        Vector3f expectedVec = new Vector3f(9, -5, 2);

        AffineTransform affineTransformation = new Transformation(new Translator(9, -5, 2));
        Vector3f vec = new Vector3f(0, 0, 0);
        System.out.println("Transformed Vector: " + affineTransformation);

        Assertions.assertTrue(expectedVec.equals(affineTransformation.transform(vec)));
    }
}
