<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- Bootstrap CSS -->
<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css">

<title>Éditeur de sous-titres</title>
</head>
<body>
	<nav class="navbar navbar-dark bg-dark">
		<span class="navbar-brand mb-0 h1">Éditeur de sous-titres</span>
	</nav>

	<div class="container mt-4">
		<p>Bienvenue dans cet éditeur de sous-titres dédié à la traduction de
			fichier srt. Veuillez choisir un fichier à traduire.</p>

		<form action="select" method="post" enctype="multipart/form-data">
			<div class="input-group">
				<select class="custom-select" id="inputSelect" name="selectedFile">
					<option selected value="lastEdited">Dernier fichier modifié</option>
					<option value="" disabled />
					<c:forEach items="${ filenames }" var="filename" varStatus="status">
						<option value="${ filename }">${ filename }</option>
					</c:forEach>
				</select>
				<div class="input-group-append">
					<button class="btn btn-outline-secondary" type="submit" name="select">Suivant</button>
				</div>
			</div>
			
			<p><br><br>Vous pouvez aussi télécharger un nouveau fichier à traduire.</p>

			<div class="input-group">
				<div class="custom-file">
					<input type="file" name="inputFile" id="inputFile" accept=".srt">
				</div>
				<div class="input-group-append">
					<button type="submit" name="upload">Télécharger</button>
				</div>
			</div>
		</form>
	</div>


	<!-- JavaScript -->
	<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js"></script>
	<script
		src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js"></script>
	<script
		src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/js/bootstrap.min.js"></script>
</body>
</html>