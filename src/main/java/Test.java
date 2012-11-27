
public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String imageCode=")( dddE6'GZ";
		imageCode=imageCode.replace(")(", "X");
		imageCode =imageCode.replaceAll("[^0-9a-zA-Z]", "");
		System.out.println(imageCode);
	}

}
