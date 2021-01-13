package org.valkyrienskies.mod.common.collision;

import lombok.Getter;
import net.minecraft.util.math.AxisAlignedBB;
import org.joml.Matrix4dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.mod.common.ships.ship_transform.ShipTransform;
import valkyrienwarfare.api.TransformType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The basis for the entire collision engine, this implementation of Polygon stores normals as well
 * as vertices and supports transformations, creating AABB, and checking for collision with other
 * Polygon objects. The polygon can theoretically support an arbitrary amount of vertices and
 * normals, but typically eight vertices and three normals are used. Only supports convex polygons.
 *
 * @author thebest108
 */
public class Polygon {

    @Getter
    private final Vector3dc[] vertices;
    private AxisAlignedBB enclosedBBCache;

    public Polygon(@Nonnull AxisAlignedBB bb, @Nullable ShipTransform transformation, @Nullable TransformType transformType) {
        Vector3d[] verticesMutable = getCornersForAABB(bb);
        if (transformation != null && transformType != null) {
            transform(verticesMutable, transformation, transformType);
        }
        this.vertices = verticesMutable;
        this.enclosedBBCache = null;
    }

    public Polygon(@Nonnull AxisAlignedBB bb) {
        this(bb, null, null);
    }

    public Polygon(@Nonnull AxisAlignedBB aabb, @Nonnull Matrix4dc transform) {
        Vector3d[] verticesMutable = getCornersForAABB(aabb);
        transform(verticesMutable, transform);
        this.vertices = verticesMutable;
    }

    public static Vector3d[] generateAxisAlignedNorms() {
        return new Vector3d[]{
                new Vector3d(1.0D, 0.0D, 0.0D),
                new Vector3d(0.0D, 1.0D, 0.0D),
                new Vector3d(0.0D, 0.0D, 1.0D)
        };
    }

    private static Vector3d[] getCornersForAABB(AxisAlignedBB bb) {
        return new Vector3d[]{
                new Vector3d(bb.minX, bb.minY, bb.minZ),
                new Vector3d(bb.minX, bb.maxY, bb.minZ),
                new Vector3d(bb.minX, bb.minY, bb.maxZ),
                new Vector3d(bb.minX, bb.maxY, bb.maxZ),
                new Vector3d(bb.maxX, bb.minY, bb.minZ),
                new Vector3d(bb.maxX, bb.maxY, bb.minZ),
                new Vector3d(bb.maxX, bb.minY, bb.maxZ),
                new Vector3d(bb.maxX, bb.maxY, bb.maxZ)
        };
    }

    public double[] getProjectionOnVector(Vector3dc axis) {
        double[] distances = new double[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            distances[i] = axis.dot(vertices[i]);
        }
        return distances;
    }

    public Vector3d getCenter() {
        Vector3d center = new Vector3d();
        for (Vector3dc v : vertices) {
            center.add(v);
        }
        center.mul(1.0 / vertices.length);
        return center;
    }

    private static void transform(Vector3d[] vertices, ShipTransform transformation, TransformType transformType) {
        for (Vector3d vertex : vertices) {
            transformation.transformPosition(vertex, transformType);
        }
    }

    private static void transform(Vector3d[] vertices, Matrix4dc transform) {
        for (Vector3d vertex : vertices) {
            transform.transformPosition(vertex);
        }
    }

    public AxisAlignedBB getEnclosedAABB() {
        if (enclosedBBCache == null) {
            Vector3dc firstVertex = vertices[0];
            double mnX = firstVertex.x();
            double mnY = firstVertex.y();
            double mnZ = firstVertex.z();
            double mxX = firstVertex.x();
            double mxY = firstVertex.y();
            double mxZ = firstVertex.z();
            for (int i = 1; i < vertices.length; i++) {
                Vector3dc vertex = vertices[i];
                mnX = Math.min(mnX, vertex.x());
                mnY = Math.min(mnY, vertex.y());
                mnZ = Math.min(mnZ, vertex.z());
                mxX = Math.max(mxX, vertex.x());
                mxY = Math.max(mxY, vertex.y());
                mxZ = Math.max(mxZ, vertex.z());
            }
            enclosedBBCache = new AxisAlignedBB(mnX, mnY, mnZ, mxX, mxY, mxZ);
        }
        return enclosedBBCache;
    }

}