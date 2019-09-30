\NeedsTeXFormat{LaTeX2e}														% Tells the compiler which version of LaTeX the package is for.
\ProvidesClass{plantilla_memorias}[2016/04/21 Template for UC3M work report]	% Gives the compiler some information about your package.
\LoadClass{article}																% Our template is based on article.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Plantilla de estilos para memorias de prácticas UC3M
%			Universidad Carlos III de Madrid
% Autor: Aitor Alonso Núñez
% Última actualización: 14 de Noviembre de 2016
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%%%% Required Packages %%%%%
\RequirePackage[pdftex, usenames, dvipsnames]{color}	% So that we can define colors and change color fonts.
\RequirePackage[table,xcdraw]{xcolor}
%\RequirePackage{xcolor}									% More colors
\RequirePackage{ragged2e}								% To allign some lines.
\RequirePackage[T1]{fontenc}							% Because we use vectorial fonts (type-1).
\usepackage[scaled]{helvet}
\renewcommand\familydefault{\sfdefault}
\RequirePackage{courier}								% To add code snippets
\RequirePackage{geometry}								% To define paper format and general margins.
\RequirePackage{graphicx}								% To add images and graphics.
\RequirePackage[spanish,es-noindentfirst]{babel}		% Language format package (Spanish).
\RequirePackage{setspace}								% Used when we modify spacing between lines.
\RequirePackage{fancyhdr}								% So that we can add header and footer.
\RequirePackage{titlesec}								% Useful to set title styles
\RequirePackage{afterpage}								% To change backgroud colors per page.
\RequirePackage{float}									% Important on figures
\RequirePackage[utf8]{inputenc}
\RequirePackage{multirow}
\RequirePackage{xkeyval}
\RequirePackage{textcomp}								% To write symbols like €
 
%%%%% Setting margins %%%%%
\setlength{\headheight}{50pt}
\setlength{\footskip}{30pt}
\geometry{verbose,a4paper,tmargin=40mm,bmargin=40mm,lmargin=25mm,rmargin=20mm}

\onehalfspace 										% Spacing between lines.
\setlength{\parindent}{0cm}	   						% Indent length.

\def\changemargin#1#2{\list{}{\rightmargin#2\leftmargin#1}\item[]}
\let\endchangemargin=\endlist						%To change margins of a few lines.

%%%%% Colors %%%%%
%Colors (the same used at the Students Delegation)
\definecolor{deleGray}{rgb}{0.2509803922, 0.2509803922, 0.2509803922}
\definecolor{deleGrayLight}{rgb}{0.4980392157, 0.4980392157, 0.4980392157}
%Color UC3M corporative identity
\definecolor{uc3mDarkBlue}{RGB}{0, 7, 121}
\definecolor{uc3mBlue}{RGB}{32, 85, 138}
\definecolor{uc3mLightBlue}{RGB}{99, 158, 200}
\definecolor{uc3mNewBlue}{RGB}{0, 29, 133}

%%%%% Algunos booleanos para encuadrar bien el encabezado %%%%%
\newif\ifcoautoresNumCuatro
\newif\ifcorreo

%%%%% Coautores y su NIA %%%%%
\newcommand{\coautoresDos}[6]{
	\def\almItres{#1}%
	\def\niaItres{#2}%
	\def\grupoItres{#3}%
   	\def\almIItres{#4}%
    \def\niaIItres{#5}%
   	\def\grupoIItres{#6}%
}

\newcommand{\coautoresCuatro}[8]{
	\def\almI{#1}%
	\def\niaI{#2}%
   	\def\almII{#3}%
    \def\niaII{#4}%
    \def\almIII{#5}%
    \def\niaIII{#6}%
    \def\almIV{#7}%
    \def\niaIV{#8}%
}

%%%%% Datos basicos de la memoria (titulacion, asignatura, titulo) %%%%%
\newcommand{\datos}[3]{
	\def\titulacion{#1}
	\def\asignatura{#2}
	\def\titulo{#3}
}

%%%%% Direcciones de correo electrónico %%%%
\newcommand{\emails}[2]{
	\def\emailI{#1}
	\def\emailII{#2}
}

%%%%% Header %%%%%
\newcommand{\header}[0]{
			% #1 NombreApellidos1 #2 NIU1 #3 NombreApellidos2 #4 NIU2
	\ifcoautoresNumCuatro
	\lhead{
		\textcolor{deleGray}{
		\vspace*{-\baselineskip} % Para eliminar espacios encima y debajo de la tabla del header
		\begin{tabular}{ll}
		Aitor Alonso Núñez & NIA 100346169 \\
    	\almI & \niaI \\
    	\almII & \niaII \\
    	\almIII & \niaIII \\
    	\almIV & \niaIV \\
		\end{tabular}}
	}
	\else
	\lhead{
		\textcolor{deleGray}{
		\vspace*{-\baselineskip} % Para eliminar espacios encima y debajo de la tabla del header		
		\begin{tabular}{lll}
    	Aitor Alonso Núñez & NIA 100346169 & Gr. 83\\
    	\almItres & \niaItres & \grupoItres \\
    	\almIItres & \niaIItres & \grupoIItres \\
		\end{tabular}}
	}	
	\fi

	\rhead{
	\ifcoautoresNumCuatro
	\includegraphics[scale=0.12]{../.memo/nueva_identidad/logo_completo_horizontal.png}	% Logo image uc3m
	\else
	\vspace*{-\baselineskip} % Para eliminar espacios encima y debajo de la tabla del header	
	\begin{tabular}{c}
	\includegraphics[scale=0.12]{../.memo/nueva_identidad/logo_completo_horizontal.png}	% Logo image uc3m
	\end{tabular}
	\fi
	}
	\renewcommand{\headrulewidth}{0pt}		% To erase header line
}

%%%%% Footer %%%%%
\newcommand{\footer}[0]{						
	\renewcommand{\footrulewidth}{0.1pt}		% Añadimos una línea que separe el pie de página del resto del documento.
	\cfoot{\vspace{1mm}\textcolor{uc3mNewBlue}{Universidad Carlos III de Madrid - Escuela Politécnica Superior}
	\textcolor{uc3mNewBlue}{\\\titulacion		
	\\\asignatura}}
	\rfoot{\vspace{1mm} Página \thepage} 		% Página actual
}

%%%%% Title page %%%%%
\newcommand{\titleMain}[0]{
	\begin{titlepage}
	~\\[0.1 cm]
	\begin{center}
	\textcolor{uc3mNewBlue}{\rule{\textwidth}{3px}} 
	{\Huge {\textcolor{deleGray}{\fontseries{bx}{\selectfont \titulo}}}	
	~\\[0.1 cm]
	\textcolor{uc3mNewBlue}{\rule{\textwidth}{3px}}}
	~\\[0.1 cm]
	\Large \mdseries\textcolor{deleGray}{
	\textbf{\titulacion}	
	\textbf{\\\asignatura}}
	\end{center}
	\vspace{5mm}
	\begin{flushright}
	\ifcoautoresNumCuatro
	\begin{tabular}{ r r }
	\large{Aitor Alonso Núñez} & \large{NIA 100346169}\\
	\large{\almI} & \large{\niaI}\\			% Se añaden coautores
	\large{\almII} & \large{\niaII}\\			
	\large{\almIII} & \large{\niaIII}\\		
	\large{\almIV} & \large{\niaIV}\\
	\end{tabular}
	\else
	\ifcorreo
	\begin{tabular}{ r r r }
	\large{Aitor Alonso Núñez} & \large{NIA 100346169} & \large{Gr. 83}\\
	\multicolumn{2}{r}{\large{\emailI}}\\\\
	\large{\almItres} & \large{\niaItres} & \large{\grupoItres}\\			% Se añaden coautores
	\multicolumn{2}{r}{\large{\emailII}}\\		
	\end{tabular}
	\else
	\begin{tabular}{rrr}
	\large{Aitor Alonso Núñez} & \large{NIA 100346169} & \large{Gr. 83}\\
	\large{\almItres} & \large{\niaItres} & \large{\grupoItres}\\			% Se añaden coautores
	\large{\almIItres} & \large{\niaIItres} & \large{\grupoIItres}\\			
	\end{tabular}
	\fi
	\fi
	\end{flushright}
	\vspace{1cm}
	\begin{center}
	\includegraphics[scale=0.25]{../.memo/nueva_identidad/logo_completo_vertical.png}
	\end{center}
	\end{titlepage}
	\newpage
}

%%%%% Section style %%%%%
%\titleformat{\section}{ 
%	\raggedright\LARGE\bfseries\color{deleGray}{}}
%	{}{0em}                     							 % Can be used to give a prefix to all sections, like 'Section ...'
% 	{}                          							 % Can be used to insert code before the heading
%	[\color{deleGrayLight}{\titlerule[2pt]}\vspace{0.3cm}]
\titleformat{\section}{
	\large\bfseries\color{uc3mNewBlue}{}}
	{}{0em}
  	{}
  	[\color{uc3mNewBlue}{\titlerule[2pt]}\vspace{0.3cm}]
 	
%%%%% Subsection style %%%%%
%\titleformat{\subsection}{
%	\large\bfseries\color{uc3mBlue}{}}
%	{}{0em}
% 	{}
\titleformat{\subsection}{
	\normalsize\bfseries\color{uc3mNewBlue}{}}
	{}{0em}
  	{}


%%%%% Subsubsection style %%%%%
%\titleformat{\subsubsection}{
%	\normalsize\bfseries\color{uc3mLightBlue}{}}
%	{}{0em}
% 	{}
\titleformat{\subsubsection}{
	\normalsize\color{uc3mNewBlue}{}}
	{}{0em}
  	{}
  	
%%%%% Text style %%%%%
\newcommand{\p}{\justify\mdseries\normalsize{\hspace{0cm}}} %	\justify\mdseries\normalsize{\hspace{0cm}}}
