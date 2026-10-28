package org.drani.postmorterm.faults;

import java.util.Set;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Control plane the injector uses to toggle faults on a running service. Never part of the evidence agents see. */
@RestController
@RequestMapping("/_faults")
public class FaultController {

    private final FaultSwitchboard switchboard;

    public FaultController(FaultSwitchboard switchboard) {
        this.switchboard = switchboard;
    }

    @GetMapping
    public Set<FaultType> active() {
        return switchboard.active();
    }

    @PostMapping("/{fault}")
    public void activate(@PathVariable FaultType fault) {
        switchboard.activate(fault);
    }

    @DeleteMapping("/{fault}")
    public void clear(@PathVariable FaultType fault) {
        switchboard.clear(fault);
    }
}
