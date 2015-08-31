#!/bin/sh
#
# Bare bones command line regression test.
# If you run it twice in a row without restarting the server, 
# some expected values will, of course, vary.
#
ADDR="http://localhost/api/notes"

echo    ==== Add 1st record... expect the record back with id=1
		cURL -i -H "Accept: application/json" -X POST -d '{"body" : "Dont forget to pick up eggs!"}' ${ADDR}
echo
echo    =========================================================
echo
echo    ==== Add 2nd record... expect the record back with id=2
		cURL -i -H "Content-Type: application/json" -X POST -d '{"body" : "Dont forget to pick up laundry!"}' ${ADDR}
echo
echo    =========================================================
echo
echo    ==== Add 3rd record... expect the record back with id=3
		cURL -i -H "Accept: application/json" -X POST -d '{"body" : "Pick up lasagna too"}' ${ADDR}
echo
echo    =========================================================
echo
echo    ==== Lookup record 1
		cURL -i -H "Accept: application/json" -X GET ${ADDR}/1  
echo
echo    =========================================================
echo
echo    ==== Lookup record 3
		cURL -i -H "Accept: application/json" -X GET ${ADDR}/3  
echo
echo    =========================================================
echo
echo    ==== Lookup non-existent record 0
		cURL -i -H "Accept: application/json" -X GET ${ADDR}/0 
echo
echo    =========================================================
echo
echo    ==== Lookup non-existent record 4
		cURL -i -H "Accept: application/json" -X GET ${ADDR}/4 
echo
echo    =========================================================
echo
echo    ==== Lookup non-existent record 200000000
		cURL -i -H "Accept: application/json" -X GET ${ADDR}/200000000
echo
echo    =========================================================
echo
echo    ==== Lookup non-existent record -1
		cURL -i -H "Accept: application/json" -X GET ${ADDR}/-1
echo
echo    =========================================================
echo
echo    ==== Retrieve all records
		cURL -i -H "Accept: application/json" -X GET ${ADDR} 
echo
echo    =========================================================
echo
echo    ==== Query subset of records... expect 1st 2 back because they include the word forget
		cURL -i -H "Accept: application/json" -X GET ${ADDR}?query=forget  
echo
echo    =========================================================
echo