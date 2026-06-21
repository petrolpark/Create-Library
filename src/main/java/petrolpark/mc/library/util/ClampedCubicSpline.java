package petrolpark.mc.library.util;

import java.util.List;
import java.util.ArrayList;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ClampedCubicSpline {

    // Input vectors
    protected Vec3 startTangent;
    protected Vec3 endTangent;
    protected List<Vec3> controlPoints;
    // Input parameter
    public final double segmentLength; // Size of a segment

    // Intermediate data
    protected double totalLength = 0d;

    // Results
    protected ArrayList<Vec3> points;
    protected ArrayList<Vec3> tangents;
    protected AABB occupiedVolume;
    
    public ClampedCubicSpline(List<Vec3> controlPoints, Vec3 startDir, Vec3 endDir, double segmentLength) {
        this.controlPoints = controlPoints;
        startTangent = startDir;
        endTangent = endDir;
        this.segmentLength = segmentLength;
        recalculate();
    };

    protected ClampedCubicSpline(Vec3 start, Vec3 end, Vec3 startDir, Vec3 endDir, double segmentLength) {
        controlPoints = new ArrayList<>();
        controlPoints.add(start);
        controlPoints.add(end);
        startTangent = startDir;
        endTangent = endDir;
        this.segmentLength = segmentLength;
    };

    public Vec3 getStartTangent() {
        return startTangent;
    };

    public Vec3 getEndTangent() {
        return endTangent;
    };

    public List<Vec3> getControlPoints() {
        return controlPoints;
    };

    public List<Vec3> getPoints() {
        return points;
    };

    public List<Vec3> getTangents() {
        return tangents;
    };

    public double getLength() {
        return totalLength;
    };

    public AABB getOccupiedVolume() {
        return occupiedVolume;
    };
 
    public void addControlPoint(Vec3 controlPoint) {
        controlPoints.add(controlPoints.size() - 1, controlPoint);
        recalculate();  
    };

    public void addInterpolatedControlPoint(int index) {
        controlPoints.add(index, controlPoints.get(index - 1).add(controlPoints.get(index)).scale(0.5d));
        recalculate();
    };

    public void removeControlPoint(int index) {
        controlPoints.remove(index);
        recalculate();
    };

    public boolean moveControlPoint(int controlPointIndex, Vec3 newLocation) {
        if (newLocation.distanceToSqr(controlPoints.get(controlPointIndex)) < 1 / 64f) return false;
        controlPoints.set(controlPointIndex, newLocation);
        recalculate();
        return true;
    };

    public void recalculate() {
        int n = controlPoints.size() - 1;

        // Put in tangents
        Vec3[] controlTangents = new Vec3[controlPoints.size()];
        controlTangents[0] = startTangent;
        controlTangents[n] = endTangent;

        // Calculate intermediate tangents (using finite differences)
        for (int i = 1; i < n; i++) {
            Vec3 diff = controlPoints.get(i+1).subtract(controlPoints.get(i-1));
            controlTangents[i] = diff.scale(0.5d); // Average of neighbors
        };

        // Compute arc lengths for each segment
        double[] lengths = new double[n];
        totalLength = 0d;
        for (int i = 0; i < n; i++) {
            lengths[i] = approximateSegmentLength(controlPoints.get(i), controlPoints.get(i+1), controlTangents[i], controlTangents[i + 1], 10);
            totalLength += lengths[i];
        };

        // Calculate total number of points
        int pointCount = (int) (1d + totalLength / segmentLength);

        // Calculate points per segment
        List<Integer> segmentPointCounts = new ArrayList<>();
        double density = pointCount / totalLength;
        for (double length : lengths) {
            segmentPointCounts.add((int)Math.max(1, Math.round(length * density)));
        };

        // Populate points and determine occupied volume
        points = new ArrayList<>();
        tangents = new ArrayList<>();
        occupiedVolume = new AABB(controlPoints.get(0), controlPoints.get(controlPoints.size()-1));
        for (int i = 0; i < n; i++) {
            Vec3 p0 = controlPoints.get(i);
            Vec3 p1 = controlPoints.get(i+1);
            Vec3 m0 = controlTangents[i];
            Vec3 m1 = controlTangents[i + 1];

            int segmentPointCount = segmentPointCounts.get(i);
            for (int j = 0; j < segmentPointCount; j++) {
                double t = (double) j / (segmentPointCount - 1);
                Vec3 point = interpolateSegment(p0, p1, m0, m1, t);
                Vec3 tangent = interpolateTangent(p0, p1, m0, m1, t).normalize();
                points.add(point);
                tangents.add(tangent);
                occupiedVolume = MathsHelper.expandToInclude(occupiedVolume, point);
                forEachSegment(i, point, tangent);
            };
        };
    };

    protected void forEachSegment(int index, Vec3 point, Vec3 tangent) {};

    private static double approximateSegmentLength(Vec3 p0, Vec3 p1, Vec3 m0, Vec3 m1, int samples) {
        double length = 0d;
        Vec3 prevPoint = p0;
        for (int i = 1; i <= samples; i++) {
            double t = (double) i / samples;
            Vec3 currentPoint = interpolateSegment(p0, p1, m0, m1, t);
            length += currentPoint.subtract(prevPoint).length();
            prevPoint = currentPoint;
        };
        return length;
    };

    private static Vec3 interpolateSegment(Vec3 p0, Vec3 p1, Vec3 m0, Vec3 m1, double t) {
        double t2 = t * t;
        double t3 = t2 * t;

        double h00 = 2 * t3 - 3 * t2 + 1; // Hermite basis function
        double h10 = t3 - 2 * t2 + t;
        double h01 = -2 * t3 + 3 * t2;
        double h11 = t3 - t2;

        return p0.scale(h00).add(m0.scale(h10)).add(p1.scale(h01)).add(m1.scale(h11));
    };

    private static Vec3 interpolateTangent(Vec3 p0, Vec3 p1, Vec3 m0, Vec3 m1, double t) {
        double t2 = t * t;

        double h00 = 6 * t2 - 6 * t; // Derivative of Hermite basis function
        double h10 = 3 * t2 - 4 * t + 1;
        double h01 = -6 * t2 + 6 * t;
        double h11 = 3 * t2 - 2 * t;

        return p0.scale(h00).add(m0.scale(h10)).add(p1.scale(h01)).add(m1.scale(h11));
    };

};
