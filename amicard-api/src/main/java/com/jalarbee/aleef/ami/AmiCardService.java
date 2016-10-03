package com.jalarbee.aleef.ami;

import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;

import static com.lightbend.lagom.javadsl.api.Service.named;

/**
 * @author Abdoulaye Diallo
 */
public interface AmiCardService extends Service {

    @Override
    default Descriptor descriptor() {

        return named("amicard").withCalls(

        ).withAutoAcl(true);

    }
}
