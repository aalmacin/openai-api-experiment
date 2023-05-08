package com.raidrin.openaiexperiment;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CountryInfo {
    private String name;
    private String capital;
    private String continent;
    private String population;
    private String area;
    private String currency;
    private List<String> languages;
}
