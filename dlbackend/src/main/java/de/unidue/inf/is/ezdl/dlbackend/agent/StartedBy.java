/*
 * Copyright 2009-2011 Universität Duisburg-Essen, Working Group
 * "Information Engineering"
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.unidue.inf.is.ezdl.dlbackend.agent;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.unidue.inf.is.ezdl.dlbackend.agent.handlers.RequestHandler;
import de.unidue.inf.is.ezdl.dlcore.message.content.MessageContent;



/**
 * This annotation is used to tag RequestHandler classes with the MessageContent
 * classes that start it.
 * <p>
 * E.g. if a RequestHandler is specialized to handle login requests, the handler
 * class would have to be annotated with <code>StartedBy(LoginAsk.class)</code>.
 * This would mean that the RequestHandlerFactory would route all incoming
 * messages that have a LoginAsk content either to an already running
 * RequestHandler with the same request ID as the message's or to a new
 * RequestHandler that has the annotation <code>StartedBy(LoginAsk.class)</code>.
 * 
 * @author mjordan
 * @see Reusable
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface StartedBy {

    /**
     * The list of MessageContent classes that should trigger the
     * {@link RequestHandler} that is annotated with this annotation.
     * 
     * @return the list of classes
     */
    Class<? extends MessageContent>[] value();
}
