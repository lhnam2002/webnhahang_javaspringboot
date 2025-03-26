package spring.dacn.mercury.constants;

import lombok.AllArgsConstructor;
@AllArgsConstructor
public enum Provider {
    LOCAL("Local"),
    GOOGLE("Google");
    public final String value;
}
