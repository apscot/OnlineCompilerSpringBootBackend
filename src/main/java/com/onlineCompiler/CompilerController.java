package com.onlineCompiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.onlineCompiler.Model.InputModel;
import com.onlineCompiler.Model.OutputModel;

@RestController
public class CompilerController {

	@RequestMapping(value = "/compile", method = RequestMethod.POST , consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> createProduct(@RequestBody InputModel inputModel) {

		try {
			return new ResponseEntity<>(onlineCompile(inputModel), HttpStatus.OK);

		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<>("An Error has occoured : File creation problem", HttpStatus.BAD_REQUEST);
		}
	}

	private OutputModel onlineCompile(InputModel inputModel) throws IOException {

		File file = null;
		OutputModel outputModel = new OutputModel();

		try {
			FileWriter myWriter = new FileWriter("Work.java");
			myWriter.write(inputModel.getCode());
			System.out.println(inputModel.getCode());
			myWriter.close();

			file = new File("Work.java");
			myWriter = new FileWriter("input.txt");
			myWriter.write(inputModel.getStdIn());
			myWriter.close();

			file = new File("input.txt");

			System.out.println("Successfully wrote to the file.");
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

		ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "javac Work.java && java -cp . Work");
		builder.redirectInput(file);
		Process p = builder.start();
		outputModel.setStdErr(streamReaderOutput(p.getErrorStream()));
		outputModel.setStdOut(streamReaderOutput(p.getInputStream()));
		System.out.println(outputModel.getStdErr());

		return outputModel;
	}

	private static String streamReaderOutput(InputStream is) throws IOException {
		
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		String line;
		String output="";
		while (true) {
			
			line = r.readLine();
			
			if (line == null) {
				break;
			}
			output = output +line;
		}
		return output;
	}
	
	@RequestMapping(value = "/products")
	   public ResponseEntity<Object> getProduct() {
		
		InputModel inputModel = new InputModel();
		inputModel.setCode("Hello");
		inputModel.setStdIn("hii");
	      return new ResponseEntity<>(inputModel, HttpStatus.OK);
	   }
}
