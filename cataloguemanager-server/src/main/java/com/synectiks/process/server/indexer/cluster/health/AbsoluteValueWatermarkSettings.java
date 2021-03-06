/*
 * */
package com.synectiks.process.server.indexer.cluster.health;

import com.google.auto.value.AutoValue;
import javax.annotation.Nullable;

@AutoValue
public abstract class AbsoluteValueWatermarkSettings implements WatermarkSettings<ByteSize> {

    public abstract SettingsType type();

    public abstract ByteSize low();

    public abstract ByteSize high();

    @Nullable
    public abstract ByteSize floodStage();

    public static class Builder {
        private SettingsType type = SettingsType.ABSOLUTE;
        private ByteSize low;
        private ByteSize high;
        private ByteSize floodStage;

        public Builder(){}

        public Builder low(ByteSize low) {
            this.low = low;
            return this;
        }

        public Builder high(ByteSize high) {
            this.high = high;
            return this;
        }

        public Builder floodStage(ByteSize floodStage) {
            this.floodStage = floodStage;
            return this;
        }

        public AbsoluteValueWatermarkSettings build() {
            return new AutoValue_AbsoluteValueWatermarkSettings(type, low, high, floodStage);
        }
    }
}
