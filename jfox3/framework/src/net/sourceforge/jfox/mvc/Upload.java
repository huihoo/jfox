package net.sourceforge.jfox.mvc;

/**
 * @author <a href="mailto:yy.young@gmail.com">Young Yang</a>
 */
public class Upload {

    //TODO: 参考 easyJWeb

    /*
    public static WebForm creatWebForm(HttpServletRequest request,
			String formName) {
		Map textElement = new HashMap();
		Map fileElement = new HashMap();
		String contentType = request.getContentType();
		String reMethod = request.getMethod();
		if ((contentType != null)
				&& (contentType.startsWith("multipart/form-data"))
				&& (reMethod.equalsIgnoreCase("POST"))) {
			// 二进制 multipart/form-data
			DiskFileUpload df = new DiskFileUpload();
			df.setHeaderEncoding(request.getCharacterEncoding());
			df.setSizeMax(1024 * 1024 * 5);
			df.setSizeThreshold(1024 * 20);
			List reqPars = null;
			try {
				reqPars = df.parseRequest(request);
				for (int i = 0; i < reqPars.size(); i++) {
					FileItem it = (FileItem) reqPars.get(i);
					if (it.isFormField()) {
						textElement.put(it.getFieldName(), it.getString(request
								.getCharacterEncoding()));// 文本字段需要转码
					} else {
						fileElement.put(it.getFieldName(), it);// 文件不需要转码
					}
				}
			} catch (Exception e) {
				logger.error(e);
			}
		} else if ((contentType != null) && contentType.equals("text/xml")) {
			StringBuffer buffer = new StringBuffer();
			try {
				String s = request.getReader().readLine();
				while (s != null) {
					buffer.append(s + "\n");
					s = request.getReader().readLine();
				}
			} catch (Exception e) {
				logger.error(e);
			}

			textElement.put("xml", buffer.toString());
		} else {
			textElement = request2map(request);
		}
		// logger.debug("表单数据处理完毕！");
		WebForm wf = findForm(formName);
		if (wf != null) {
			wf.setFileElement(fileElement);
			wf.setTextElement(textElement);
		}
		return wf;
	}
	*/

    public static void main(String[] args) {

    }
/*
    public     void   doPost(HttpServletRequest request, HttpServletResponse response)
            throws  ServletException, IOException {
       response.setContentType( " text/html " );
       response.setCharacterEncoding( " gbk " );
       PrintWriter out  =  response.getWriter();
       out.println( " <html> " );
       out.println( "   <head><title>提示</title></head> " );
       out.println( "   <body> " );
        //  不用获取 URL 对象也行，直接用 getServletContext().getRealPath("/") 代替。
       URL url  =  getServletContext().getResource( " / " );
        //  从 HTTP servlet 获取 fileupload 组件需要的内容
       RequestContext requestContext  =   new  ServletRequestContext(request);
        //  判断是否包含 multipart 内容
        if  (ServletFileUpload.isMultipartContent(requestContext)) {
            //  创建基于磁盘的文件工厂
           DiskFileItemFactory factory  =   new  DiskFileItemFactory();
            //  设置直接存储文件的极限大小，一旦超过则写入临时文件以节约内存。默认为 1024 字节
           factory.setSizeThreshold( 1024   *   1024 );
            //  创建上传处理器，可以处理从单个 HTML 上传的多个上传文件。
           ServletFileUpload upload  =   new  ServletFileUpload(factory);
            //  最大允许上传的文件大小
           upload.setSizeMax( 1024   *   1024 );
            //  处理上传
           List items  =   null ;
            try  {
               items  =  upload.parseRequest(requestContext);
                //  由于提交了表单字段信息，需要进行循环区分。
                for  ( int  i  =   0 ; i  <  items.size(); i ++ ) {
                   FileItem fi  =  (FileItem) items.get(i);
                    //  如果不是表单内容，取出 multipart。
                    if  ( ! fi.isFormField()) {
                        //  上传文件路径和文件、扩展名。
                       String sourcePath  =  fi.getName();
                       String[] sourcePaths  =  sourcePath.split( " \\\\ " );
                        //  获取真实文件名
                       String fileName  =  sourcePaths[sourcePaths.length  -   1 ];
                        //  创建一个待写文件
                       File uploadedFile  =   new  File( new  URI(url.toString() + fileName));
                        //  写入
                       fi.write(uploadedFile);
                       out.println(fileName + " 上传成功。 " );
                   }
               }
           }  catch  (Exception e) {
               out.println( " 上传失败，请检查上传文件大小是否超过1兆，并保证在上传时该文件没有被其他程序占用。 " );
               out.println( " <br>原因： " + e.toString());
               e.printStackTrace();
           }
       }
       out.println( "   </body> " );
       out.println( " </html> " );
       out.flush();
       out.close();
   }
*/
}
