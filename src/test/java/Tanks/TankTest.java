package Tanks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class TankTest {
    @Test
    public void testTankConstructor() {
        // Create a new Tank object
        String type = "Heavy";
        float x = 10.0f;
        float y = 5.0f;
        Tank tank = new Tank(type, x, y);

        // Verify the fields are initialized correctly
        assertEquals(type, tank.type);
        assertEquals(x, tank.x, 0.001f); // Use delta for float comparison
        assertEquals(y, tank.y, 0.001f);
        assertEquals(x, tank.turretCenterX, 0.001f); // turretCenterX should match x
        assertEquals(640 - (y * App.CELLSIZE) - App.CELLSIZE / 32 * 7 - 1, tank.turretCenterY, 0.001f);
        assertEquals((float)Math.PI, tank.turretAngle, 0.001f);
        assertEquals(0, tank.speed, 0.001f); // Initial speed should be 0
        assertEquals(3, tank.parachute);
        assertEquals(50, tank.power);
    }

    
    public Tank tank;

    public void setUp() {
        // Initialize a Tank object for testing
        tank = new Tank("A", 400.0f, 200.0f);
        tank.speed = 100.0f; // Set an initial speed for testing
        tank.falling = false; // Assume tank is not falling for basic tests
        tank.parachute = 2; // Set initial parachute count
        tank.falling_speed = 0.0f; // Set initial falling speed
        tank.newY = 300.0f; // Set newY value for falling condition
    }

    @Test
    public void testUpdate_XGreaterThan863() {
        // Test when tank's x coordinate is greater than 863
        tank.x = 900.0f;
        tank.update();
        assertEquals(0.0f, tank.speed, 0.001f); // Speed should be reset to 0
    }

    @Test
    public void testUpdate_XLessThan1() {
        // Test when tank's x coordinate is less than 1
        tank.x = -10.0f;
        tank.update();
        assertEquals(0.0f, tank.speed, 0.001f); // Speed should be reset to 0
    }

    @Test
    public void testUpdate_XPlusSpeedExceeds863() {
        // Test when tank's x + speed / App.FPS exceeds 863
        tank.x = 800.0f;
        tank.speed = 100.0f; // Adjust speed to cause x + speed / App.FPS > 863
        tank.update();
        assertEquals(0.0f, tank.speed, 0.001f); // Speed should be reset to 0
    }

    @Test
    public void testUpdate_XPlusSpeedLessThan1() {
        // Test when tank's x + speed / App.FPS is less than 1
        tank.x = 10.0f;
        tank.speed = -100.0f; // Adjust speed to cause x + speed / App.FPS < 1
        tank.update();
        assertEquals(0.0f, tank.speed, 0.001f); // Speed should be reset to 0
    }

    @Test
    public void testUpdate_NormalMovement() {
        // Test normal movement scenario
        tank.x = 400.0f;
        float initialY = tank.y;
        tank.update();
        float expectedX = 400.0f + 100.0f / App.FPS; // Assuming speed is 100.0f
        float expectedY = App.smoothMountlevel.get(Math.round(expectedX));
        assertEquals(expectedX, tank.x, 0.001f); // Check new x coordinate
        assertEquals(expectedY, tank.y, 0.001f); // Check new y coordinate
        assertEquals(expectedX, tank.turretCenterX, 0.001f); // Check turretCenterX
        float expectedTurretCenterY = 640 - (expectedY * App.CELLSIZE) - App.CELLSIZE / 32 * 7 - 1;
        assertEquals(expectedTurretCenterY, tank.turretCenterY, 0.001f); // Check turretCenterY
    }

    @Test
    public void testUpdate_FallingWithParachute() {
        // Test falling scenario with remaining parachute
        tank.x = 400.0f;
        tank.y = 250.0f; // Start falling from y = 250.0f
        tank.falling = true;
        tank.update();
        float expectedY = 250.0f + 60.0f / App.FPS; // Assuming falling_speed is 60.0f
        assertEquals(expectedY, tank.y, 0.001f); // Check new y coordinate
        assertEquals(1, tank.parachute); // Parachute count should decrease by 1
    }

    @Test
    public void testInExplode_Outside() {
        // Create a Tank object for testing
        Tank tank = new Tank("Heavy", 400.0f, 300.0f);

        // Test a point outside the tank's explosion radius
        assertFalse(tank.inExplode(500.0f, 400.0f));
    }

    @Test
    public void testInExplode_Inside() {
        // Create a Tank object for testing
        Tank tank = new Tank("Heavy", 400.0f, 300.0f);

        // Test a point inside the tank's explosion radius
        assertTrue(tank.inExplode(400.0f, 300.0f));
    }

    @Test
    public void testInExplode_OnEdge() {
        // Create a Tank object for testing
        Tank tank = new Tank("Heavy", 400.0f, 300.0f);

        // Test a point on the edge of the tank's explosion radius
        assertTrue(tank.inExplode(400.0f + 30, 300.0f));
    }

    public void testStop() {
        // Create a Tank object for testing
        Tank tank = new Tank("Heavy", 400.0f, 300.0f);
        tank.speed = 50.0f; // Set speed to a non-zero value

        // Call the stop() method
        tank.stop();

        // Verify that speed is set to 0
        assertEquals(0.0f, tank.speed, 0.001f);
    }

    @Test
    public void testAdjustTurretAngle_PositiveChange() {
        // Create a Tank object for testing
        Tank tank = new Tank("Heavy", 400.0f, 300.0f);
        float initialAngle = tank.turretAngle;

        // Call adjustTurretAngle() with a positive angle change
        float angleChange = (float) Math.toRadians(30); // Convert 30 degrees to radians
        tank.adjustTurretAngle(angleChange);

        // Verify that turret angle increases by the expected amount
        assertEquals(initialAngle + angleChange, tank.turretAngle, 0.001f);
    }

    @Test
    public void testAdjustTurretAngle_NegativeChange() {
        // Create a Tank object for testing
        Tank tank = new Tank("Heavy", 400.0f, 300.0f);
        float initialAngle = tank.turretAngle;

        // Call adjustTurretAngle() with a negative angle change
        float angleChange = (float) Math.toRadians(-45); // Convert -45 degrees to radians
        tank.adjustTurretAngle(angleChange);

        // Verify that turret angle decreases by the expected amount but remains positive
        float expectedAngle = (initialAngle + angleChange >= 0) ? (initialAngle + angleChange) : 0;
        assertEquals(expectedAngle, tank.turretAngle, 0.001f);
    }

    @Test
    public void testNormalizeAngle_PositiveAngleWithinRange() {
        // Test a positive angle within the range [0, 2π)
        float angle = (float) Math.PI / 2; // 90 degrees in radians
        float normalizedAngle = Tank.normalizeAngle(angle);
        assertEquals(angle, normalizedAngle, 0.001f); // Angle should remain unchanged
    }

    @Test
    public void testNormalizeAngle_PositiveAngleOutsideRange() {
        // Test a positive angle outside the range [0, 2π)
        float angle = (float) (3 * Math.PI); // 540 degrees in radians (outside range)
        float expectedNormalizedAngle = (float) Math.PI; // Expected normalized angle (180 degrees)
        float normalizedAngle = Tank.normalizeAngle(angle);
        assertEquals(expectedNormalizedAngle, normalizedAngle, 0.001f); // Angle should be normalized to 180 degrees
    }

    @Test
    public void testNormalizeAngle_NegativeAngleWithinRange() {
        // Test a negative angle within the range [0, 2π)
        float angle = (float) (-Math.PI); // -180 degrees in radians
        float expectedNormalizedAngle = (float) Math.PI; // Expected normalized angle (180 degrees)
        float normalizedAngle = Tank.normalizeAngle(angle);
        assertEquals(expectedNormalizedAngle, normalizedAngle, 0.001f); // Angle should be normalized to 180 degrees
    }

    @Test
    public void testNormalizeAngle_NegativeAngleOutsideRange() {
        // Test a negative angle outside the range [0, 2π)
        float angle = (float) (-4 * Math.PI); // -720 degrees in radians (outside range)
        float expectedNormalizedAngle = (float) (2 * Math.PI); // Expected normalized angle (360 degrees)
        float normalizedAngle = Tank.normalizeAngle(angle);
        assertEquals(expectedNormalizedAngle, normalizedAngle, 0.001f); // Angle should be normalized to 360 degrees
    }

    @Test
    public void testNormalizeAngle_ZeroAngle() {
        // Test angle of zero radians
        float angle = 0.0f;
        float normalizedAngle = Tank.normalizeAngle(angle);
        assertEquals(angle, normalizedAngle, 0.001f); // Angle should remain unchanged (0 radians)
    }

    @Test
    public void testNormalizeAngle_LargePositiveAngle() {
        // Test a large positive angle (greater than 2π)
        float angle = (float) (6 * Math.PI); // 1080 degrees in radians
        float expectedNormalizedAngle = (float) (2 * Math.PI); // Expected normalized angle (360 degrees)
        float normalizedAngle = Tank.normalizeAngle(angle);
        assertEquals(expectedNormalizedAngle, normalizedAngle, 0.001f); // Angle should be normalized to 360 degrees
    }

    @Test
    public void testMove_PositiveSpeed() {
        // Create a Tank object for testing
        Tank tank = new Tank("Heavy", 400.0f, 300.0f);

        // Call move() method with a positive speed
        float speed = 50.0f;
        tank.move(speed);

        // Verify that the tank's speed is set correctly
        assertEquals(speed, tank.speed, 0.001f);
    }

    @Test
    public void testMove_ZeroSpeed() {
        // Create a Tank object for testing
        Tank tank = new Tank("Heavy", 400.0f, 300.0f);
        tank.speed = 100.0f; // Set an initial speed

        // Call move() method with zero speed to stop the tank
        tank.move(0.0f);

        // Verify that the tank's speed is set to 0
        assertEquals(0.0f, tank.speed, 0.001f);
    }

    @Test
    public void testAdjustTurretPower_IncreasePower() {
        // Create a Tank object for testing
        Tank tank = new Tank("Heavy", 400.0f, 300.0f);
        float initialPower = tank.power;
        float amount = 20.0f;

        // Call adjustTurretPower() to increase power
        tank.adjustTurretPower(amount);

        // Verify that the power is increased by the expected amount
        assertEquals(initialPower + amount / 30.0f, tank.power, 0.001f);
    }

    @Test
    public void testAdjustTurretPower_DecreasePower() {
        // Create a Tank object for testing
        Tank tank = new Tank("Heavy", 400.0f, 300.0f);
        float initialPower = tank.power;
        float amount = -10.0f;

        // Call adjustTurretPower() to decrease power
        tank.adjustTurretPower(amount);

        // Verify that the power is decreased by the expected amount but not below 0
        float expectedPower = Math.max(initialPower + amount / 30.0f, 0.0f);
        assertEquals(expectedPower, tank.power, 0.001f);
    }

    @Test
    public void testAdjustTurretPower_MaximumPower() {
        // Create a Tank object for testing
        Tank tank = new Tank("Heavy", 400.0f, 300.0f);
        tank.power = 80; // Set power close to maximum hp

        float amount = 100.0f;

        // Call adjustTurretPower() to increase power beyond max hp
        tank.adjustTurretPower(amount);

        // Verify that the power is capped at max hp
        assertEquals(tank.hp, tank.power, 0.001f);
    }

    


    void testAdjustTurretAngle() {

    }

    @Test
    void testAdjustTurretPower() {

    }

    @Test
    void testFalling() {

    }

    @Test
    void testFireProjectile() {

    }

    @Test
    void testInExplode() {

    }

    @Test
    void testMove() {

    }

    @Test
    void testNormalizeAngle() {

    }

    @Test
    void testUpdate() {

    }
}
