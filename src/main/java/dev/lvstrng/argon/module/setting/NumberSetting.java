package dev.lvstrng.argon.module.setting;

public final class NumberSetting extends Setting<Double> {
	
	private double min;
	
	private double max;
	
	private double increment;
	
	private final double originalValue;

	public NumberSetting(CharSequence name, double min, double max, double value, double increment) {
		super(name, value);
		this.min = min;
		this.max = max;
		this.increment = increment;
		this.originalValue = value;
	}

	public double getOriginalValue() {
		return originalValue;
	}

	public double getIncrement() {
		return increment;
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	public int getValueInt() {
		return getValue().intValue();
	}

	public float getValueFloat() {
		return getValue().floatValue();
	}

	public long getValueLong() {
		return getValue().longValue();
	}

	@Override
	public void setValue(Double value) {
		double precision = 1.0D / this.increment;
		super.setValue(Math.round(Math.max(this.min, Math.min(this.max, value)) * precision) / precision);
	}

	@Override
	public NumberSetting setDescription(CharSequence desc) {
		super.setDescription(desc);
		return this;
	}
}
