<%@ tag language="java" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head>
		<title>${pageContext.session.servletContext.servletContextName}</title>		
		<link rel="stylesheet" type="text/css" href="css/addressbook.css" media="all"/>		
		<link rel="stylesheet" type="text/css" href="css/framework-specific-addressbook.css" media="all"/>		
	</head>	
	<body>
			
		<jsp:doBody />
		
	</body>	    
</html>
