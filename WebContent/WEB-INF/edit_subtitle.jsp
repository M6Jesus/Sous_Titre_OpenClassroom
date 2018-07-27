<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- Bootstrap CSS -->
<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css">

<title>Éditeur de sous-titres</title>
</head>
<body style="min-height: 75rem; padding-top: 4.5rem;">

	<!-- Nav Bar -->
	<nav class="navbar navbar-expand-md fixed-top navbar-dark bg-dark">
		<span class="navbar-brand mb-0 h1">
			<c:out value="${ filename }"> Unamed </c:out>
		</span>
		<ul class="nav navbar-nav navbar-left">
			<li>
				<a class="nav-link" href="select">Changer de fichier</a>
			</li>
		</ul>

		<button class="navbar-toggler collapsed" type="button" data-toggle="collapse"
			data-target="#downloadLinks" aria-controls="downloadLinks"
			aria-expanded="false" aria-label="Toggle navigation">
			<span class="navbar-toggler-icon"></span>
		</button>
		<div class="collapse navbar-collapse" id="downloadLinks">
			<ul class="navbar-nav ml-auto">
				<li class="nav-item">
					<a class="nav-link disabled" href="#">Export</a>
				</li>
				<li class="nav-item">
					<a class="nav-link ${ disabled }" href="${ originalURL }" target="_blank">Original</a>
				</li>
				<li class="nav-item">
					<a class="nav-link ${ disabled }" href="${ translationURL }" target="_blank">Traduction</a>
				</li>
			</ul>
		</div>
	</nav>
	
	<c:if test="${ message != null }">
		<p> <c:out value="${ message }" /> </p>
	</c:if>

	<form id="mainForm" name="mainForm" action="edit" method="post"
		enctype="multipart/form-data" accept-charset="UTF-8">


		<div class="container mt-4 ml-lg-1">
				<c:forEach items="${ sequences[0] }" var="sequence" varStatus="status">
					
					<div class="row">
						<div class="col text-center">
							<c:out value="${ sequence.index }" />
						</div>
					</div>
					<div class="row">
						<div class="col text-center">
							<c:out value="${ sequence.times }" />
						</div>
					</div>
					<c:forEach items="${ sequence.lines }" var="line" varStatus="seqStatus">
						<c:set var="initialValue"
							value="${ sequences[1][status.index].lines[seqStatus.index] }" />
						<div class="row align-items-center">
							<div class="col-sm text-center text-md-right">
								<c:out value="${ line }" />
							</div>
							<div class="col-sm">
								<input class="form-control" 
								type="text" 
								value="${ initialValue eq '.' ? '' : initialValue }"
								name="seq-${ sequence.index }" 
								size="35" />
							</div>
						</div>
					</c:forEach>
				</c:forEach>
			
		</div>

		<nav class="navbar navbar-expand-lg fixed-bottom navbar-light bg-light jus">
			<button class="navbar-toggler" type="button" data-toggle="collapse"
				data-target="#paginationBar" aria-controls="paginationBar"
				aria-expanded="false" aria-label="Toggle navigation">
				<span class="navbar-toggler-icon"></span>
			</button>
			<div class="collapse navbar-collapse" id="paginationBar">
				<div class="container">
					<div class="row align-items-center">
						<div class="col offset-md-2 d-none d-lg-block">
							<ul class="navbar-nav float-right">
								<li class="page-item ${ page - 1 <= 0 ? 'disabled' : '' }">
									<a class="page-link" href="${baseURL}&page=${page - 5}"
										aria-label="Previous" onclick="return submitForm;">
										<span aria-hidden="true">&laquo;</span>
										<span class="sr-only">Previous</span>
									</a>
								</li>
								<c:forEach var="i" begin="${ pageBegin }" end="${ pageEnd }">
									<li class="page-item ${ i == page ? 'active' : '' }">
										<a class="page-link" href="${baseURL}&page=${i}"
											onclick="return submitForm;">${i}</a>
									</li>
								</c:forEach>
								<li class="page-item ${ page + 1 > nbPages ? 'disabled' : '' }">
									<a class="page-link" href="${baseURL}&page=${page + 5}"
										aria-label="Next" onclick="return submitForm;">
										<span aria-hidden="true">&raquo;</span>
										<span class="sr-only">Next</span>
									</a>
								</li>
							</ul>
						</div>
						<div class="col-sm input-group">
							<input class="form-control" type="number" name="page" id="page"
								placeholder="page" aria-label="page" value="${ page  }" min="1" max="${ nbPages }"
								onkeypress="handlePage(event)" style="max-width: 80px;">
							<div class="input-group-append">
								<label class="input-group-text" for="page">
									<c:out value="/ ${ nbPages }" />
								</label>
							</div>
						</div>
						<div class="col-sm input-group">
							<input class="form-control" type="number" name="sequencesPerPage"
								id="sequencesPerPage" placeholder="sequences/page" aria-label="sequences/page"
								size="3" value="${ sequencesPerPage  }" min="1"
								onkeypress="handleSPP(event)" style="max-width: 60px;">
							<div class="input-group-append">
								<label class="input-group-text" for="sequencesPerPage">SPP</label>
							</div>
						</div>
					</div>
				</div>
			</div>
			<span class="navbar-brand mb-0">
				<button type="button" id="save" name="save"
					class="btn btn-outline-secondary float-right" disabled>Enregistrer</button>
			</span>
		</nav>

	</form>

	<!-- JavaScript -->
	<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js"></script>
	<script
		src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/js/bootstrap.min.js"></script>
	<script>
		var unsaved = false;

		$(document).ready(function() {
			$("input[type='text']").click(function() {
				unsaved = true;
				$('button').prop('disabled', false);
			});

			$("#save").click(function() {
				unsaved = false;
				$("#save").prop('disabled', true);
				$("#mainForm").submit();
			});

			$("#mainForm").submit(function(e) {
				var field = $(document.createElement("input"));
				field.attr("name", "filename").attr("type", "hidden");
				field.val("${ filename }");
				$(this).append(field);

				var field = $(document.createElement("input"));
				field.attr("name", "page").attr("type", "hidden");
				field.val("${ page }");
				$(this).append(field);
			});

			window.onbeforeunload = unloadPage;
		});

		function unloadPage() {
			if (unsaved) {
				return "You have unsaved changes on this page. Do you want to leave this page and discard your changes or stay on this page?";
			}
		}

		function updateQueryString(key, value, url) {
			if (!url)
				url = window.location.href;

			var re = new RegExp("([?&])" + key + "=.*?(&|#|$)", "i");
			if (url.match(re)) {
				return url.replace(re, '$1' + key + "=" + value + '$2');
			} else {
				var hash = '';
				if (url.indexOf('#') !== -1) {
					hash = url.replace(/.*#/, '#');
					url = url.replace(/#.*/, '');
				}
				var separator = url.indexOf('?') !== -1 ? "&" : "?";
				return url + separator + key + "=" + value + hash;
			}
		}

		function handlePage(e) {
			if (e.keyCode == 13) {
				window.location.href = updateQueryString("page", document
						.getElementById("page").value);
			}
		}

		function handleSPP(e) {
			if (e.keyCode == 13) {
				window.location.href = updateQueryString("sequencesPerPage",
						document.getElementById("sequencesPerPage").value);
			}
		}
	</script>
</body>
</html>