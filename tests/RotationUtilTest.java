import org.junit.jupiter.api.Test;
import utility.RotationUtil;

class RotationUtilTest {

    @Test
    void turnLeftOrRight() {

        assert (RotationUtil.turnLeftOrRight(0, 90, 360) > 0);
        assert (RotationUtil.turnLeftOrRight(-350, 350, 360)  < 0);
        assert (RotationUtil.turnLeftOrRight(350, -350, 360)  > 0);
        assert (RotationUtil.turnLeftOrRight(90, 230, 360)  > 0);
        assert (RotationUtil.turnLeftOrRight(0, 179, 360)  > 0);
        assert (RotationUtil.turnLeftOrRight(179, -1, 360) == 180);
    }
}