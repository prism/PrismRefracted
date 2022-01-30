/*
 * Prism (Refracted)
 *
 * Copyright (c) 2022 M Botsko (viveleroi)
 *                    Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package network.darkhelmet.prism.actions;

import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTEntity;

import java.util.Locale;

import network.darkhelmet.prism.api.actions.IEntityAction;
import network.darkhelmet.prism.api.actions.types.ActionType;
import network.darkhelmet.prism.api.activities.IActivity;
import network.darkhelmet.prism.api.modifications.ModificationResult;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Nullable;

public class EntityAction extends Action implements IEntityAction {
    /**
     * The nbt container.
     */
    private NBTContainer nbtContainer;

    /**
     * The entity type.
     */
    private EntityType entityType;

    /**
     * Construct a new entity action.
     *
     * @param type The action type
     * @param entity The entity
     */
    public EntityAction(ActionType type, Entity entity) {
        super(type);

        this.entityType = entity.getType();
        this.nbtContainer = new NBTContainer(new NBTEntity(entity).getCompound().toString());

        // Strip some data we don't want to track/rollback.
        String[] rejects = {
            "DeathTime",
            "Fire",
            "Health",
            "HurtByTimestamp",
            "HurtTime",
            "OnGround",
            "Pos",
            "UUID",
            "WorldUUIDLeast",
            "WorldUUIDMost"
        };
        for (String reject : rejects) {
            nbtContainer.removeKey(reject);
        }
    }

    /**
     * Construct a new entity action with the type and nbt container.
     *
     * @param type The action type
     * @param entityType The entity type
     * @param container The nbt container
     */
    public EntityAction(ActionType type, EntityType entityType, NBTContainer container) {
        super(type);

        this.entityType = entityType;
        this.nbtContainer = container;
    }

    @Override
    public String serializeEntityType() {
        return entityType.toString().toLowerCase(Locale.ENGLISH);
    }

    @Override
    public String formatContent() {
        return entityType.toString().toLowerCase(Locale.ENGLISH).replace("_", " ");
    }

    @Override
    public boolean hasCustomData() {
        return this.nbtContainer != null;
    }

    @Override
    public @Nullable String serializeCustomData() {
        return nbtContainer.toString();
    }

    @Override
    public ModificationResult applyRollback(IActivity activityContext, boolean isPreview) {
        Location loc = activityContext.location();
        if (loc.getWorld() != null && entityType.getEntityClass() != null) {
            loc.getWorld().spawn(loc, entityType.getEntityClass(), entity -> {
                new NBTEntity(entity).mergeCompound(nbtContainer);
            });

            return ModificationResult.APPLIED;
        }

        return ModificationResult.SKIPPED;
    }

    @Override
    public ModificationResult applyRestore(IActivity activityContext, boolean isPreview) {
        return ModificationResult.SKIPPED;
    }
}
