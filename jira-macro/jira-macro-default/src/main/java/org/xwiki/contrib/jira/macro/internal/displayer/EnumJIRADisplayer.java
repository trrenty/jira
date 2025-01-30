/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.contrib.jira.macro.internal.displayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jdom2.Element;
import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.jira.macro.JIRAField;
import org.xwiki.contrib.jira.macro.JIRAFields;
import org.xwiki.contrib.jira.macro.JIRAMacroParameters;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.ParagraphBlock;
import org.xwiki.rendering.block.SpaceBlock;
import org.xwiki.rendering.transformation.MacroTransformationContext;

/**
 * Displays JIRA issues next to each other (like an enumeration) in inline mode.
 *
 * @version $Id$
 * @since 4.2M1
 */
@Component
@Named("enum")
@Singleton
public class EnumJIRADisplayer extends AbstractJIRADisplayer
{
    /**
     * Default list of JIRA fields to display.
     */
    private static final JIRAFields FIELDS = new JIRAFields(Arrays.asList(JIRAField.STATUS, JIRAField.KEY));

    @Override
    public List<Block> display(Collection<Element> issues, JIRAMacroParameters parameters)
    {
        List<Block> blocks = new ArrayList<>();

        JIRAFields fields = normalizeFields(parameters);
        Iterator<Element> issueIt = issues.iterator();
        while (issueIt.hasNext()) {
            Element issue = issueIt.next();
            Iterator<JIRAField> it = fields.iterator();
            while (it.hasNext()) {
                JIRAField field = it.next();
                // Use the displayer for the field
                blocks.addAll(getFieldDisplayer(field).displayField(field, issue, parameters));
                // Add space to separate fields, unless we're on the last field
                if (it.hasNext()) {
                    blocks.add(new SpaceBlock());
                }
            }
            // Add space to separate issues, unless we're on the last field
            if (issueIt.hasNext()) {
                blocks.add(new SpaceBlock());
            }
        }
        return blocks;
    }

    @Override
    public List<Block> display(Collection<Element> issue, JIRAMacroParameters parameters,
        MacroTransformationContext context)
    {
        List<Block> result = display(issue, parameters);
        return context.isInline() ? result : Collections.singletonList(new ParagraphBlock(result));
    }

    @Override
    protected JIRAFields getDefaultFields()
    {
        return FIELDS;
    }
}
