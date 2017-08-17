package com.example.yoush.canvasanim.view;

/**
 * Helpful utils.
 */
class Utils {
	private Utils() {}

	/**
	 * Convert square of magnitude to decibels
	 * @param squareMag square of magnitude
	 * @return decibels
	 */
	static float magnitudeToDb(float squareMag) {
		if (squareMag == 0)
			return 0;
		return (float) (20 * Math.log10(squareMag));
	}
}
