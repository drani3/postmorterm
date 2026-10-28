package org.aditya.postmorterm.harness.agent;

import org.aditya.postmorterm.harness.tools.Toolbox;

/** Something that investigates one incident through the toolbox and commits to a root cause. */
public interface Agent {

    String name();

    Diagnosis investigate(Toolbox tools);
}
