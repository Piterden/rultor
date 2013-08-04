/**
 * Copyright (c) 2009-2013, rultor.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the rultor.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.rultor.web;

import com.jcabi.aspects.Loggable;
import com.jcabi.log.Logger;
import com.rexsl.page.BaseResource;
import com.rexsl.page.inset.FlashInset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Maps constraint violations to JAX-RS responses.
 *
 * <p>The class is mutable and NOT thread-safe.
 *
 * @author Yegor Bugayenko (yegor@tpc.com)
 * @version $Id$
 * @since 1.0
 */
@Provider
@Loggable(Loggable.DEBUG)
public final class ConstraintsMapper extends BaseResource
    implements ExceptionMapper<ConstraintViolationException> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Response toResponse(final ConstraintViolationException violation) {
        String msg = violation.getMessage();
        if (msg == null) {
            msg = "undisclosed constraint violation (internal problem)";
        }
        final Collection<String> violations =
            new ArrayList<String>(violation.getConstraintViolations().size());
        for (ConstraintViolation<?> vio : violation.getConstraintViolations()) {
            violations.add(vio.getMessage());
        }
        return Response.fromResponse(
            FlashInset.forward(
                this.uriInfo().getRequestUri(),
                Logger.format(
                    "%s: %[list]s",
                    msg,
                    violations
                ),
                Level.WARNING
            ).getResponse()
        ).entity(ExceptionUtils.getRootCauseStackTrace(violation)).build();
    }

}
