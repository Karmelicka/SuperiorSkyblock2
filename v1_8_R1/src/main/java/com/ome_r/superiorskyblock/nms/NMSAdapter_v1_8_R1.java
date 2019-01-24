package com.ome_r.superiorskyblock.nms;

import com.mojang.authlib.properties.Property;
import com.ome_r.superiorskyblock.SuperiorSkyblock;
import com.ome_r.superiorskyblock.island.Island;
import com.ome_r.superiorskyblock.utils.key.Key;
import com.ome_r.superiorskyblock.utils.jnbt.CompoundTag;
import com.ome_r.superiorskyblock.wrappers.WrappedPlayer;
import net.minecraft.server.v1_8_R1.Chunk;
import net.minecraft.server.v1_8_R1.EntityHuman;
import net.minecraft.server.v1_8_R1.EntityLiving;
import net.minecraft.server.v1_8_R1.EntityPlayer;
import net.minecraft.server.v1_8_R1.EnumWorldBorderAction;
import net.minecraft.server.v1_8_R1.IBlockData;
import net.minecraft.server.v1_8_R1.ItemStack;
import net.minecraft.server.v1_8_R1.NBTTagByte;
import net.minecraft.server.v1_8_R1.NBTTagByteArray;
import net.minecraft.server.v1_8_R1.NBTTagCompound;
import net.minecraft.server.v1_8_R1.NBTTagDouble;
import net.minecraft.server.v1_8_R1.NBTTagFloat;
import net.minecraft.server.v1_8_R1.NBTTagInt;
import net.minecraft.server.v1_8_R1.NBTTagIntArray;
import net.minecraft.server.v1_8_R1.NBTTagList;
import net.minecraft.server.v1_8_R1.NBTTagLong;
import net.minecraft.server.v1_8_R1.NBTTagShort;
import net.minecraft.server.v1_8_R1.NBTTagString;
import net.minecraft.server.v1_8_R1.PacketPlayOutMapChunk;
import net.minecraft.server.v1_8_R1.PacketPlayOutWorldBorder;
import net.minecraft.server.v1_8_R1.TileEntityFlowerPot;
import net.minecraft.server.v1_8_R1.TileEntityMobSpawner;
import net.minecraft.server.v1_8_R1.World;
import net.minecraft.server.v1_8_R1.BlockPosition;
import net.minecraft.server.v1_8_R1.Block;

import net.minecraft.server.v1_8_R1.WorldBorder;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_8_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftItemStack;
import org.bukkit.entity.LivingEntity;

import java.util.Optional;
import java.util.Set;

@SuppressWarnings("unused")
public class NMSAdapter_v1_8_R1 implements NMSAdapter {

    private SuperiorSkyblock plugin = SuperiorSkyblock.getPlugin();

    @Override
    public int getCombinedId(Location location) {
        World world = ((CraftWorld) location.getWorld()).getHandle();
        IBlockData blockData = world.getType(new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        return Block.getCombinedId(blockData);
    }

    @Override
    public void setBlock(Location location, int combinedId) {
        World world = ((CraftWorld) location.getWorld()).getHandle();
        Chunk chunk = world.getChunkAt(location.getChunk().getX(), location.getChunk().getZ());
        BlockPosition blockPosition = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        chunk.a(blockPosition, Block.getByCombinedId(combinedId));
    }

    @Override
    public org.bukkit.inventory.ItemStack getFlowerPot(Location location) {
        World world = ((CraftWorld) location.getWorld()).getHandle();
        BlockPosition blockPosition = new BlockPosition(location.getX(), location.getY(), location.getZ());
        TileEntityFlowerPot tileEntityFlowerPot = (TileEntityFlowerPot) world.getTileEntity(blockPosition);
        ItemStack itemStack = new ItemStack(tileEntityFlowerPot.b(), 1, tileEntityFlowerPot.c());
        return CraftItemStack.asBukkitCopy(itemStack);
    }

    @Override
    public void setFlowerPot(Location location, org.bukkit.inventory.ItemStack itemStack) {
        World world = ((CraftWorld) location.getWorld()).getHandle();
        BlockPosition blockPosition = new BlockPosition(location.getX(), location.getY(), location.getZ());
        TileEntityFlowerPot tileEntityFlowerPot = (TileEntityFlowerPot) world.getTileEntity(blockPosition);
        ItemStack flower = CraftItemStack.asNMSCopy(itemStack);
        tileEntityFlowerPot.a(flower.getItem(), flower.getData());
        tileEntityFlowerPot.update();
    }

    @Override
    public CompoundTag getNBTTag(org.bukkit.inventory.ItemStack bukkitStack) {
        ItemStack itemStack = CraftItemStack.asNMSCopy(bukkitStack);
        NBTTagCompound nbtTagCompound = itemStack.hasTag() ? itemStack.getTag() : new NBTTagCompound();
        return CompoundTag.fromNBT(nbtTagCompound);
    }

    @Override
    public org.bukkit.inventory.ItemStack getFromNBTTag(org.bukkit.inventory.ItemStack bukkitStack, CompoundTag compoundTag) {
        ItemStack itemStack = CraftItemStack.asNMSCopy(bukkitStack);
        itemStack.setTag((NBTTagCompound) compoundTag.toNBT());
        return CraftItemStack.asBukkitCopy(itemStack);
    }

    @Override
    public CompoundTag getNBTTag(LivingEntity livingEntity) {
        EntityLiving entityLiving = ((CraftLivingEntity) livingEntity).getHandle();
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        entityLiving.b(nbtTagCompound);
        return CompoundTag.fromNBT(nbtTagCompound);
    }

    @Override
    public void getFromNBTTag(LivingEntity livingEntity, CompoundTag compoundTag) {
        EntityLiving entityLiving = ((CraftLivingEntity) livingEntity).getHandle();
        NBTTagCompound nbtTagCompound = (NBTTagCompound) compoundTag.toNBT();
        if(nbtTagCompound != null)
            entityLiving.a(nbtTagCompound);
    }

    @Override
    @Deprecated
    public Key getBlockKey(ChunkSnapshot chunkSnapshot, int x, int y, int z) {
        Material type = Material.getMaterial(chunkSnapshot.getBlockTypeId(x, y, z));
        short data = (short) chunkSnapshot.getBlockData(x, y, z);
        return Key.of(type, data);
    }

    @Override
    public int getSpawnerDelay(CreatureSpawner creatureSpawner) {
        Location location = creatureSpawner.getLocation();
        TileEntityMobSpawner mobSpawner = (TileEntityMobSpawner)((CraftWorld) location.getWorld())
                .getTileEntityAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        return mobSpawner.getSpawner().spawnDelay;
    }

    @Override
    public void refreshChunk(org.bukkit.Chunk bukkitChunk) {
        World world = ((CraftWorld) bukkitChunk.getWorld()).getHandle();
        //noinspection ConstantConditions
        Chunk chunk = ((CraftChunk) bukkitChunk).getHandle();
        for(Object object : world.players)
            ((EntityPlayer) object).playerConnection.sendPacket(new PacketPlayOutMapChunk(chunk, true, 65535));
    }

    @Override
    public void setWorldBorder(WrappedPlayer wrappedPlayer, Island island) {
        if(!plugin.getSettings().worldBordersEnabled)
            return;

        boolean disabled = !wrappedPlayer.hasWorldBorderEnabled();

        WorldBorder worldBorder = new WorldBorder();

        worldBorder.world = ((CraftWorld) wrappedPlayer.getWorld()).getHandle();
        double size = disabled || island == null ? Integer.MAX_VALUE : island.getIslandSize();
        worldBorder.a(size, size, 0L);

        Location center = island == null ? wrappedPlayer.getLocation() : island.getCenter();

        if (wrappedPlayer.getWorld().getEnvironment() == org.bukkit.World.Environment.NETHER) {
            worldBorder.c(center.getX() * 8, center.getZ() * 8);
        } else {
            worldBorder.c(center.getX(), center.getZ());
        }

        PacketPlayOutWorldBorder packetPlayOutWorldBorder = new PacketPlayOutWorldBorder(worldBorder, EnumWorldBorderAction.INITIALIZE);
        ((CraftPlayer) wrappedPlayer.asPlayer()).getHandle().playerConnection.sendPacket(packetPlayOutWorldBorder);
    }

    @Override
    public void setSkinTexture(WrappedPlayer wrappedPlayer) {
        EntityPlayer entityPlayer = ((CraftPlayer) wrappedPlayer.asPlayer()).getHandle();
        Optional<Property> optional = entityPlayer.getProfile().getProperties().get("textures").stream().findFirst();
        optional.ifPresent(property -> wrappedPlayer.setTexture(property.getValue()));
    }

    @Override
    public byte[] getNBTByteArrayValue(Object object) {
        return ((NBTTagByteArray) object).c();
    }

    @Override
    public byte getNBTByteValue(Object object) {
        return ((NBTTagByte) object).f();
    }

    @Override
    public Set<String> getNBTCompoundValue(Object object) {
        //noinspection unchecked
        return (Set<String>) ((NBTTagCompound) object).c();
    }

    @Override
    public double getNBTDoubleValue(Object object) {
        return ((NBTTagDouble) object).g();
    }

    @Override
    public float getNBTFloatValue(Object object) {
        return ((NBTTagFloat) object).h();
    }

    @Override
    public int[] getNBTIntArrayValue(Object object) {
        return ((NBTTagIntArray) object).c();
    }

    @Override
    public int getNBTIntValue(Object object) {
        return ((NBTTagInt) object).d();
    }

    @Override
    public Object getNBTListIndexValue(Object object, int index) {
        return ((NBTTagList) object).g(index);
    }

    @Override
    public long getNBTLongValue(Object object) {
        return ((NBTTagLong) object).c();
    }

    @Override
    public short getNBTShortValue(Object object) {
        return ((NBTTagShort) object).e();
    }

    @Override
    public String getNBTStringValue(Object object) {
        return ((NBTTagString) object).a_();
    }
}
