package com.petrolpark.client.outline;

import javax.annotation.Nonnull;

import org.joml.Vector3f;
import org.joml.Vector4f;

import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.outliner.Outline;
import net.createmod.catnip.render.PonderRenderTypes;
import net.createmod.catnip.render.SuperRenderTypeBuffer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

@RequiresCreate
public class CuboidOutline extends Outline {

    protected final Vector3f start = new Vector3f(0f, 0f, 0f);
    protected final Vector3f end = new Vector3f(0f, 0f, 0f);

	public CuboidOutline set(AABB box) {
		start.set(box.minX, box.minY, box.minZ);
		end.set(box.maxX, box.maxY, box.maxZ);
		return this;
	};

    public CuboidOutline set(Vector3f start, Vector3f end) {
		this.start.set(start.x, start.y, start.z);
		this.end.set(end.x, end.y, end.z);
		return this;
	};

	public CuboidOutline set(Vec3 start, Vec3 end) {
		this.start.set(start.x, start.y, start.z);
		this.end.set(end.x, end.y, end.z);
		return this;
	};

    @Override
    public void render(@Nonnull PoseStack poseStack, @Nonnull SuperRenderTypeBuffer buffer, @Nonnull Vec3 camera, float pt) {
        VertexConsumer consumer = buffer.getBuffer(PonderRenderTypes.outlineSolid());
		params.loadColor(colorTemp);
		Vector4f color = colorTemp;
		int lightmap = ((OutlineParamsAccessor) params).getLightmap();
		boolean disableLineNormals = ((OutlineParamsAccessor) params).getDisableLineNormals();

		diffPosTemp.set(end.x - start.x, end.y - start.y, end.z - start.z);
		poseStack.pushPose();
		TransformStack.of(poseStack)
			.translate(start.x - camera.x, start.y - camera.y, start.z - camera.z);
		maxPosTemp.set(end.x - start.x, end.y - start.y, end.z - start.z);
		bufferCuboid(poseStack.last(), consumer, minPosTemp, maxPosTemp, color, lightmap, disableLineNormals);
		poseStack.popPose();
    };
    
};
