package org.drani.postmorterm.harness.scoring;

import java.util.Objects;

import org.drani.postmorterm.bundle.GroundTruth;
import org.drani.postmorterm.harness.agent.Diagnosis;
import org.springframework.stereotype.Component;

/** Half credit for localising the faulty service, full credit for also naming the injected fault. */
@Component
public class Scorer {

    public Score score(Diagnosis diagnosis, GroundTruth truth) {
        boolean service = Objects.equals(diagnosis.service(), truth.service());
        boolean fault = service && Objects.equals(diagnosis.faultType(), truth.faultType());
        return new Score(service, fault, fault ? 1.0 : service ? 0.5 : 0.0);
    }
}
