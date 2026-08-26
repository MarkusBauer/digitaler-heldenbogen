package de.mb.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;


@JsonIgnoreProperties(ignoreUnknown = true)
public class AutoBogenConfig {
    @JsonProperty
    public ArrayList<AutoExport> exports = new ArrayList<>();


    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE
    )
    public static class AutoExport {
        @JsonProperty
        public String heldId;

        @JsonProperty
        public String path;

        /**
         * one of: "new-html", "new-pdf", "old-pdf"
         */
        @JsonProperty
        public String type;

        public AutoExport() {
        }

        public AutoExport(String heldId, String path, String type) {
            this.heldId = heldId;
            this.path = path;
            this.type = type;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            AutoExport that = (AutoExport) o;
            return Objects.equals(heldId, that.heldId) && Objects.equals(path, that.path) && Objects.equals(type, that.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(heldId, path, type);
        }

        @Override
        public String toString() {
            return "AutoBogenConfigEntry{" +
                "heldId='" + heldId + '\'' +
                ", path='" + path + '\'' +
                ", type='" + type + '\'' +
                '}';
        }

        public boolean isWriteable() {
            File f = new File(path);
            return f.exists() ? f.canWrite() : f.getParentFile().canWrite();
        }

        public boolean exists() {
            return new File(path).exists();
        }
    }
}
