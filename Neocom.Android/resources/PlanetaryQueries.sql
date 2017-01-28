select * from planetSchematicsTypeMap
where typeID = 2329
and isInput = 0


SELECT *
FROM  planetSchematics ps,
WHERE     schematicID in (79,95,102)


SELECT *
FROM  planetSchematicsTypeMap pstms, planetSchematicsTypeMap pstmt
WHERE pstms.typeID = 2329
AND   pstms.isInput = 0
AND   pstmt.schematicID = pstms.schematicID
AND   pstmT.isInput = 1


SELECT pstm.typeID, pstm.quantity, pstm.isInput
FROM   planetSchematicsTypeMap pstm
WHERE  schematicID = 79