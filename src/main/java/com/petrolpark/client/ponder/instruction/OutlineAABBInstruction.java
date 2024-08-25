package com.petrolpark.client.ponder.instruction;

import com.simibubi.create.foundation.ponder.PonderPalette;
import com.simibubi.create.foundation.ponder.PonderScene;
import com.simibubi.create.foundation.ponder.instruction.TickingInstruction;

import net.minecraft.world.phys.AABB;

public class OutlineAABBInstruction extends TickingInstruction {
    private PonderPalette color;
	private Object slot;
	private AABB aabb;

	public OutlineAABBInstruction(PonderPalette color, Object slot, AABB aabb, int ticks) {
		super(false, ticks);
		this.color = color;
		this.slot = slot;
		this.aabb = aabb;
	};

	@Override
	public void tick(PonderScene scene) {
		super.tick(scene);
		scene.getOutliner().showAABB(slot, aabb)
			.lineWidth(1 / 16f)
			.colored(color.getColor());
	};

};
