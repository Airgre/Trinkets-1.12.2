package xzeroair.trinkets.attributes.speed;

import java.util.UUID;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.math.MathHelper;
import xzeroair.trinkets.util.Reference;

public class SpeedAttribute {
	private static final String id = Reference.MODID + ".";
	private static String name = "SPRINT_SPEED";
	protected static UUID uuid = UUID.fromString("d171a59a-63dd-48eb-917a-3e02bdd6d13b");
	protected static final UUID uuidSprintChargeSpeed = UUID.fromString("b9605d5b-76cf-4a23-a7a0-d8540928bfc5");
	private static double bonus;
	private static int operation;
	private static double getBonus() {
		return bonus;
	}
	private static void setBonus(double bonus) {
		SpeedAttribute.bonus = MathHelper.clamp(bonus, -1024.0D, 1024.0D);
	}
	protected static AttributeModifier createModifier() {
		return new AttributeModifier(getUUID(), id+name, getBonus(), getOperation());
	}
	private static void setUUID(UUID uuid) {
		SpeedAttribute.uuid = uuid;
	}
	private static UUID getUUID() {
		return uuid;
	}
	private static int getOperation() {
		return operation;
	}
	private static void setOperation(int operation) {
		operation = Reference.getOpModifier(operation);
	}
	public static void addModifier(EntityLivingBase entity, double bonus, UUID uuid, int operation) {
		final IAttributeInstance AttributeInstance = entity.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED);
		if((AttributeInstance.getModifier(uuid) != null)) {
			if(AttributeInstance.getModifier(uuid).getAmount() != bonus) {
				removeModifier(entity, uuid);
			}
		}
		if(bonus != 0) {
			setOperation(operation);
			setBonus(bonus);
			setUUID(uuid);
			if(AttributeInstance.getModifier(uuid) == null) {
				AttributeInstance.applyModifier(createModifier());
			}
		}
	}
	public static void removeModifier(EntityLivingBase entity, UUID uuid) {
		final IAttributeInstance AttributeInstance = entity.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED);
		if(AttributeInstance.getModifier(uuid) != null) {
			AttributeInstance.removeModifier(uuid);
		}
	}
}