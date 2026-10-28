package org.aditya.postmorterm.harness.scoring;

public record Score(boolean serviceCorrect, boolean faultCorrect, double points) {
}
