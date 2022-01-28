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

package network.darkhelmet.prism.formatters;

import java.util.List;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;

import network.darkhelmet.prism.config.OutputConfiguration;

public class OutputFormatter {
    /**
     * Output configuration.
     */
    protected OutputConfiguration outputConfiguration;

    /**
     * Prefix.
     */
    protected Component prefix;


    public Component prefix() {
        return prefix;
    }

    /**
     * Format an error message.
     *
     * @param message The message
     * @return The formatted component
     */
    public Component error(String message) {
        return format(outputConfiguration.error(), message);
    }

    /**
     * Format a success message.
     *
     * @param message The message
     * @return The formatted component
     */
    public Component success(String message) {
        return format(outputConfiguration.success(), message);
    }

    /**
     * Format an info message.
     *
     * @param message The message
     * @return The formatted component
     */
    public Component info(String message) {
        return format(outputConfiguration.info(), message);
    }

    /**
     * Format a heading message.
     *
     * @param heading The heading segment
     * @param message The message segment
     * @return The formatted component
     */
    public Component heading(String heading, String message) {
        return format(outputConfiguration.heading(), heading, message);
    }

    /**
     * Format a subdued message.
     *
     * @param message The message
     * @return The formatted component
     */
    public Component subdued(String message) {
        return format(outputConfiguration.subdued(), message);
    }

    /**
     * Format a base message.
     *
     * @param template The base template
     * @param message The message segment
     * @return The formatted component
     */
    protected Component format(String template, String message) {
        return format(template, "", message);
    }

    /**
     * Format a base message.
     *
     * @param template The base template
     * @param heading The heading segment
     * @param message The message segment
     * @return The formatted component
     */
    protected Component format(String template, String heading, String message) {
        Template prefixTemplate = Template.of("prefix", prefix);
        Template headingTemplate = Template.of("heading", heading);
        Template messageTemplate = Template.of("message", message);

        List<Template> templates = List.of(prefixTemplate, headingTemplate, messageTemplate);
        return MiniMessage.get().parse(template, templates);
    }
}