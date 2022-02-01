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

package network.darkhelmet.prism.api.actions;

import network.darkhelmet.prism.api.actions.types.IActionType;
import network.darkhelmet.prism.api.activities.IActivity;
import network.darkhelmet.prism.api.services.modifications.ModificationResult;

public interface IAction {
    /**
     * Apply the rollback. If the action type is not reversible, this does nothing.
     *
     * @param activityContext The activity as a context
     * @param isPreview If preview only
     */
    ModificationResult applyRollback(IActivity activityContext, boolean isPreview);

    /**
     * Apply the restore. If the action type is not reversible, this does nothing.
     *
     * @param activityContext The activity as a context
     * @param isPreview If preview only
     */
    ModificationResult applyRestore(IActivity activityContext, boolean isPreview);

    /**
     * Format the content of this action for text tdisplay.
     *
     * @return The content string
     */
    String formatContent();

    /**
     * Get the action type.
     *
     * @return The action type
     */
    IActionType type();
}
