/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2015-2018 the Valkyrien Warfare team
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it.
 * Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income unless it is to be used as a part of a larger project (IE: "modpacks"), nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from the Valkyrien Warfare team.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: The Valkyrien Warfare team), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package valkyrienwarfare.mod.common.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import valkyrienwarfare.api.TransformType;
import valkyrienwarfare.deprecated_api.DummyMethods;
import valkyrienwarfare.mod.common.ValkyrienWarfareMod;
import valkyrienwarfare.mod.common.math.Vector;
import valkyrienwarfare.mod.common.physics.management.PhysicsWrapperEntity;

public class RealMethods implements DummyMethods {

    @Override
    public Vector getLinearVelocity(Entity shipEnt, double secondsToApply) {
        PhysicsWrapperEntity wrapper = (PhysicsWrapperEntity) shipEnt;
        return wrapper.getPhysicsObject().getPhysicsProcessor().linearMomentum
                .getProduct(secondsToApply * wrapper.getPhysicsObject().getPhysicsProcessor().getInvMass());
    }

    @Override
    public Vector getAngularVelocity(Entity shipEnt) {
        PhysicsWrapperEntity wrapper = (PhysicsWrapperEntity) shipEnt;
        return wrapper.getPhysicsObject().getPhysicsProcessor().angularVelocity;
    }

    // Returns the matrix which converts local coordinates (The positions of the
    // blocks in the world) to the entity coordinates (The position in front of the
    // player)
    @Override
    public double[] getShipTransformMatrix(Entity shipEnt) {
        PhysicsWrapperEntity wrapper = (PhysicsWrapperEntity) shipEnt;
        return wrapper.getPhysicsObject().getShipTransformationManager().getCurrentTickTransform().getInternalMatrix(TransformType.SUBSPACE_TO_GLOBAL);
    }

    // Note, do not call this from World coordinates; first subtract the world
    // coords from the shipEntity xyz and then call!
    @Override
    public Vector getVelocityAtPoint(Entity shipEnt, Vector inBody, double secondsToApply) {
        PhysicsWrapperEntity wrapper = (PhysicsWrapperEntity) shipEnt;
        Vector toReturn = wrapper.getPhysicsObject().getPhysicsProcessor().getVelocityAtPoint(inBody);
        toReturn.multiply(secondsToApply);
        return toReturn;
    }

    @Override
    public double getShipMass(Entity shipEnt) {
        PhysicsWrapperEntity wrapper = (PhysicsWrapperEntity) shipEnt;
        return wrapper.getPhysicsObject().getPhysicsProcessor().getMass();
    }

    @Override
    public Vector getPositionInShipFromReal(World worldObj, Entity shipEnt, Vector positionInWorld) {
        PhysicsWrapperEntity wrapper = (PhysicsWrapperEntity) shipEnt;
        Vector inLocal = new Vector(positionInWorld);
        wrapper.getPhysicsObject().getShipTransformationManager().fromLocalToGlobal(inLocal);
        return inLocal;
    }

    @Override
    public Vector getPositionInRealFromShip(World worldObj, Entity shipEnt, Vector pos) {
        PhysicsWrapperEntity wrapper = (PhysicsWrapperEntity) shipEnt;
        Vector inReal = new Vector(pos);
        wrapper.getPhysicsObject().getShipTransformationManager().fromLocalToGlobal(inReal);
        return inReal;
    }

    @Override
    public boolean isBlockPartOfShip(World worldObj, BlockPos pos) {
        return getShipEntityManagingPos(worldObj, pos) != null;
    }

    @Override
    public PhysicsWrapperEntity getShipEntityManagingPos(World worldObj, BlockPos pos) {
        PhysicsWrapperEntity wrapper = ValkyrienWarfareMod.VW_PHYSICS_MANAGER.getObjectManagingPos(worldObj, pos);
        return wrapper;
    }

    @Override
    public Vector getShipCenterOfMass(Entity shipEnt) {
        return new Vector(((PhysicsWrapperEntity) shipEnt).getPhysicsObject().getPhysicsProcessor().gameTickCenterOfMass);
    }

    @Override
    public boolean isEntityAShip(Entity entityToTest) {
        return entityToTest instanceof PhysicsWrapperEntity;
    }
}