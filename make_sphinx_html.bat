@echo off

rem Sphinxのドキュメントをビルドし、taregetのsiteフォルダに配置する。

echo "Building sphinx documentation with Docker."

%~d0
cd %~p0

rmdir /q /s .\src\site\sphinx\source\_build
docker run --rm -v %~dp0\src\site\sphinx\source:/docs xlsmapper/sphinx sphinx-build -M html . _build

rem sphinxの成果物のコピー
rmdir /q /s .\target\site\sphinx
mkdir .\target\site\sphinx
xcopy /y /e .\src\site\sphinx\source\_build\html .\target\site\sphinx

rem github-pagesのsphinx対応
echo "" > .\target\site\.nojekyll

pause
