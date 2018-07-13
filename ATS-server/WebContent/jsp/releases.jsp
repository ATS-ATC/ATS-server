<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<script src="${pageContext.request.contextPath}/jquery/jquery-3.2.1.js"></script>

<script src="${pageContext.request.contextPath}/bootstrap/js/bootstrap.min.js"></script>
<link href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.min.css" rel="stylesheet">

<title>Features</title>
</head>
<body >
	<div class="container-fluid">
		<!-- <div class="row" style="padding-top: 33px;">
			<div class="col-md-2" align="right" style="padding-top: 25px;">
				<span class="glyphicon glyphicon-tag" aria-hidden="true"></span>1.0.1
			</div>
			<div class="col-md-10" style="border-left: 1px solid #ddd;">
				<h2 style="color: #0366D6;">1.0.1</h2>
				<blockquote><footer>
				<span class="glyphicon glyphicon-user" aria-hidden="true"></span>
				<font size="2" color="gray"><a>shelhamer</a> released this on 19 Apr 2017 · 117 commits to master since this release</font>
				</footer></blockquote>
				<h3>Assets</h3>
				<div style="padding-top: 10px;">
					<span class="glyphicon glyphicon-link" aria-hidden="true"></span>
					<a>Source code (zip)</a>
				</div>
				<div style="padding-top: 10px;">
					<span class="glyphicon glyphicon-link" aria-hidden="true"></span>
					<a>Source code (tar.gz)</a>
				</div>
				<br/><br/>
				<p>
					&nbsp;&nbsp;&nbsp;&nbsp;This release marks the convergence of development into a stable, reference release of the framework and a shift into maintenance mode. Let's review the progress culminating in our 1.0:
					<ul>
						<li>research: nearly 4,000 citations, usage by award papers at CVPR/ECCV/ICCV, and tutorials at ECCV'14 and CVPR'15</li>
						<li>industry: adopted by Facebook, NVIDIA, Intel, Sony, Yahoo! Japan, Samsung, Adobe, A9, Siemens, Pinterest, the Embedded Vision Alliance, and more</li>
						<li>community: 250+ contributors, 15k+ subscribers on github, and 7k+ members of the mailing list</li>
						<li>development: 10k+ forks, >1 contribution/day on average, and dedicated branches for OpenCL and Windows</li>
						<li>downloads: 10k+ downloads and updates a month, ~50k unique visitors to the home page every two weeks, and >100k unique downloads of the reference models</li>
						<li>winner of the ACM MM open source award 2014 and presented as a talk at ICML MLOSS 2015</li>
					</ul>
					&nbsp;&nbsp;&nbsp;&nbsp;Thanks for all of your efforts leading us to Caffe 1.0! Your part in development, community, feedback, and framework usage brought us here. As part of 1.0 we will be welcoming collaborators old and new to join as members of the Caffe core.
					<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;Stay tuned for the next steps in DIY deep learning with Caffe. As development is never truly done, there's always 1.1!
					<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;Now that 1.0 is done, the next generation of the framework—Caffe2—is ready to keep up the progress on DIY deep learning in research and industry. While Caffe 1.0 development will continue with 1.1, Caffe2 is the new framework line for future development led by Yangqing Jia. Although Caffe2 is a departure from the development line of Caffe 1.0, we are planning a migration path for models just as we have future-proofed Caffe models in the past.
					
					<br/><br/>Happy brewing,<br/>The Caffe Crew<br/><br/>
					<span class="glyphicon glyphicon-grain" aria-hidden="true"></span>
				</p>
			</div>
		</div> -->
		<div class="row" style="padding-top: 33px;">
			<div class="col-md-3" align="right" style="padding-top: 25px;">
				<h2>Releases</h2>
			</div>
		</div>
		<c:forEach items="${releases}" varStatus="idx" var="releases" > 
		<div class="row" style="padding-top: 33px;">
			<div class="col-md-2" align="right" style="padding-top: 25px;">
				<span class="glyphicon glyphicon-tag" aria-hidden="true"></span>${releases.version_num }
			</div>
			<div class="col-md-10" style="border-left: 1px solid #ddd;">
				<h2 style="color: #0366D6;">${releases.version_num }</h2>
				<blockquote><footer>
				<span class="glyphicon glyphicon-user" aria-hidden="true"></span>
				<font size="2" color="gray"><a>${releases.version_owner }</a> released this on ${releases.version_date} to master since this release</font>
				</footer></blockquote>
				<c:forEach items="${releases.source_zip_urls}" varStatus="idx" var="urls" > 
					<h3>Assets</h3>
					<div style="padding-top: 10px;">
						<span class="glyphicon glyphicon-link" aria-hidden="true"></span>
						<a href="${urls.Windows }" target= _blank>Source code (Windows)</a>
					</div>
					<div style="padding-top: 10px;">
						<span class="glyphicon glyphicon-link" aria-hidden="true"></span>
						<a href="${urls.Linux }" target= _blank>Source code (Linux)</a>
					</div>
					<div style="padding-top: 10px;">
						<span class="glyphicon glyphicon-link" aria-hidden="true"></span>
						<a href="${urls.Command }" target= _blank>Source code (Command)</a>
					</div>
				</c:forEach> 
				<%-- <div style="padding-top: 10px;">
					<span class="glyphicon glyphicon-link" aria-hidden="true"></span>
					<a href="${releases.source_tar_info }">Source code (tar.gz)</a>
				</div> --%>
				
				<br/><br/>
				<p>
					<div id="version_info">${releases.version_info }</div>
					<span class="glyphicon glyphicon-grain" aria-hidden="true"></span>
				</p>
			</div>
		</div>
		</c:forEach> 
	</div>	
</body>
</html>
</body>

</html>