import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.IntStream;

public class Reader {

	/*
Band    name	Resolution (m)	Purpose
B01	    60	    Aerosol detection
B09	    60	    Water vapour
B10	    60	    Cirrus

B05	    20	    Vegetation classification
B06	    20	    Vegetation classification
B07	    20	    Vegetation classification
B08A	20	    Vegetation classification
B11	    20	    Snow / ice / cloud discrimination
B12	    20	    Snow / ice / cloud discrimination

B02	    10	    Blue
B03	    10	    Green
B04	    10	    Red
B08	    10	    Near infrared

	 */
	public static void main( String[] args ){


		InputStream stream10m = Reader.class.getClassLoader().getResourceAsStream("10m.tif");
		InputStream stream20m = Reader.class.getClassLoader().getResourceAsStream("20m.tif");
		InputStream stream60m = Reader.class.getClassLoader().getResourceAsStream("60m.tif");

		Raster image10m = getBufferedImage(stream10m);
		Raster image20m = getBufferedImage(stream20m);
		Raster image60m = getBufferedImage(stream60m);

		getRangeParallelStream(image10m.getWidth())
				.forEach(w->
						getRangeParallelStream(image10m.getHeight())
								.forEach(h -> {
									int[] values10m = new int[4];
									int[] values20m = new int[6];
									int[] values60m = new int[3];

									image10m.getPixel(w, h, values10m);
									image20m.getPixel(Math.floorDiv(w,2), Math.floorDiv(h,2), values20m);
									image60m.getPixel(Math.floorDiv(w,6), Math.floorDiv(h,6), values60m);

									String teste = values10m[0] + "," + values10m[1] + "," + values10m[2] + "," + values10m[3] + "," +values20m[0] + "," + values20m[1] + "," + values20m[2] + "," + values20m[3]+ "," + values20m[4] + "," + values20m[5]  + "," + values60m[0] + "," + values60m[1]  + "," + values60m[2] +"\n";

									writeFiles(teste);
								})
				);


	}


	private static IntStream getRangeParallelStream(int width) {
		return IntStream.range(0, width - 1).parallel();
	}

	private static void writeFiles(String teste) {
		try {
			Path pixelsValues = Paths.get("/home/luiz/Documents/estudo/pixelReader/src/main/resources/pixelsValues.csv");
			Files.write(pixelsValues, teste.getBytes(), StandardOpenOption.DSYNC, StandardOpenOption.APPEND);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private static Raster getBufferedImage(InputStream stream) {
		try {
			ImageInputStream imageStream = ImageIO.createImageInputStream(stream);
			BufferedImage image = ImageIO.read(imageStream);
			return image.getData();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}

