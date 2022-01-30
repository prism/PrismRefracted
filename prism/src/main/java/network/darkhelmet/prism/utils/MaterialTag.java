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

package network.darkhelmet.prism.utils;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.jetbrains.annotations.NotNull;

public class MaterialTag implements Tag<Material> {
    /**
     * Cache all materials.
     */
    private final EnumSet<Material> materials;

    /**
     * The namespaced key.
     */
    private final NamespacedKey key = null;

    /**
     * Constructor.
     *
     * @param materialTags Tags
     */
    @SafeVarargs
    public MaterialTag(Tag<Material>... materialTags) {
        this.materials = EnumSet.noneOf(Material.class);
        append(materialTags);
    }

    /**
     * Constructor.
     *
     * @param materials Materials
     */
    public MaterialTag(Material... materials) {
        this.materials = EnumSet.noneOf(Material.class);
        append(materials);
    }

    /**
     * Constructor.
     *
     * @param segment Sting
     * @param mode MatchMode
     */
    public MaterialTag(String segment, MatchMode mode) {
        this.materials = EnumSet.noneOf(Material.class);
        append(segment, mode);
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return key;
    }

    /**
     * Append materials.
     *
     * @param materials The materials
     * @return The material tag
     */
    public MaterialTag append(Material... materials) {
        this.materials.addAll(Arrays.asList(materials));
        return this;
    }

    /**
     * Add new Tags to the group.
     *
     * @param materialTags Tag
     * @return MaterialTag
     */
    @SafeVarargs
    public final MaterialTag append(Tag<Material>... materialTags) {
        for (Tag<Material> materialTag : materialTags) {
            this.materials.addAll(materialTag.getValues());
        }

        return this;
    }

    /**
     * Append a segment and mode.
     *
     * @param segment String
     * @param mode MatchMode
     * @return MaterialTag
     */
    public MaterialTag append(String segment, MatchMode mode) {
        segment = segment.toUpperCase();

        switch (mode) {
            case PREFIX:
                for (Material m : Material.values()) {
                    if (m.name().startsWith(segment)) {
                        materials.add(m);
                    }
                }
                break;

            case SUFFIX:
                for (Material m : Material.values()) {
                    if (m.name().endsWith(segment)) {
                        materials.add(m);
                    }
                }
                break;

            case CONTAINS:
                for (Material m : Material.values()) {
                    if (m.name().contains(segment)) {
                        materials.add(m);
                    }
                }
                break;
            default:
                throw new IllegalArgumentException(mode.name() + " is NOT a valid rule");
        }

        return this;
    }

    /**
     * Exclude certain materials.
     *
     * @param materials Materials to exclude
     * @return MaterialTag.
     */
    public MaterialTag exclude(Material... materials) {
        for (Material m : materials) {
            this.materials.remove(m);
        }

        return this;
    }

    /**
     * Exclude certain materials.
     *
     * @param materialTags Materials to exclude
     * @return MaterialTag.
     */
    @SafeVarargs
    public final MaterialTag exclude(Tag<Material>... materialTags) {
        for (Tag<Material> materialTag : materialTags) {
            this.materials.removeAll(materialTag.getValues());
        }

        return this;
    }

    /**
     * Exclude tags from this group.
     *
     * @param segment String
     * @param mode MatchMode
     * @return MaterialTag
     */
    public MaterialTag exclude(String segment, MatchMode mode) {
        segment = segment.toUpperCase();

        switch (mode) {
            case PREFIX:
                for (Material m : Material.values()) {
                    if (m.name().startsWith(segment)) {
                        materials.remove(m);
                    }
                }
                break;

            case SUFFIX:
                for (Material m : Material.values()) {
                    if (m.name().endsWith(segment)) {
                        materials.remove(m);
                    }
                }
                break;

            case CONTAINS:
                for (Material m : Material.values()) {
                    if (m.name().contains(segment)) {
                        materials.remove(m);
                    }
                }
                break;
            default:
                throw new IllegalArgumentException(mode.name() + " is NOT a valid rule");
        }

        return this;
    }

    @NotNull
    @Override
    public Set<Material> getValues() {
        return materials;
    }

    @Override
    public boolean isTagged(@NotNull Material material) {
        return materials.contains(material);
    }

    @Override
    public String toString() {
        return materials.toString();
    }

    public enum MatchMode {
        PREFIX,
        SUFFIX,
        CONTAINS
    }
}
