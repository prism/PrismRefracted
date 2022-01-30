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

package network.darkhelmet.prism.modifications;

import java.util.List;
import java.util.function.Consumer;

import network.darkhelmet.prism.api.activities.IActivity;
import network.darkhelmet.prism.api.modifications.ModificationQueueResult;
import network.darkhelmet.prism.api.modifications.ModificationResult;

public class Rollback extends AbstractWorldModificationQueue {
    /**
     * Construct a new rollback.
     *
     * @param modifications A list of modifications
     */
    public Rollback(final List<IActivity> modifications, Consumer<ModificationQueueResult> onComplete) {
        super(modifications, onComplete);
    }

    @Override
    protected ModificationResult applyModification(IActivity activity) {
        return activity.action().applyRollback(activity, isPreview);
    }
}
