package com.fluffytiger.earlygamewebapp.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "jars")
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
public class JarMeta {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter @Setter private Long id;
    @Getter @Setter private String path;
    @Getter @Setter private long size;
    @Getter @Setter private String version;
    @Enumerated(EnumType.ORDINAL)
    @Getter @Setter private OS osName;

    public String readableSize() {
        int unit = 1000;
        if (size < unit) return size + " B";
        int exp = (int) (Math.log(size) / Math.log(unit));
        String pre = "kMGTPE".charAt(exp-1) + "";
        return String.format("%.1f %sB", size / Math.pow(unit, exp), pre);
    }

    public JarMeta(String path, long size, String version, OS osName) {
        this.path = path;
        this.size = size;
        this.version = version;
        this.osName = osName;
    }
}
